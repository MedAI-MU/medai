#!/usr/bin/env python3
"""
Android String Resource Translator

Production-ready translation of Android string resources using Google Gemini API.

Features:
- Placeholder preservation with validation (%s, %1$s, etc.)
- Markup tag preservation with validation (<b>, <xliff:g>, etc.)
- Token order validation (not just presence)
- Source attribute propagation (formatted, product, tools:*)
- Conditional placeholder handling for formatted="false" strings
- Whitespace preservation (no stripping of source text)
- HTML entity conversion (case-insensitive)
- Android special character escaping
- xml:space="preserve" for significant whitespace
- Batch translation with individual fallback
- Comprehensive validation and error handling

Usage:
    # Check for missing translations (CI dry-run)
    python translate.py --mode check --locales es,de,fr

    # Apply translations
    python translate.py --mode apply --locales es,de,fr

    # Custom settings
    python translate.py --mode apply --locales es --batch-size 15 --model gemini-1.5-pro
    python translate.py --mode apply --locales ar --model gemma-3-27b-it --batch-size 15


Environment:
    GEMINI_API_KEY=your_api_key_here

CI Validation:
    After running translations, validate with:
        ./gradlew assembleDebug
    or at minimum:
        ./gradlew processDebugResources

    This catches AAPT2 errors that XML parsing alone cannot detect.
"""

from __future__ import annotations

import argparse
import json
import logging
import os
import re
import sys
import time
import xml.etree.ElementTree as ET
from dataclasses import dataclass, field
from pathlib import Path
from typing import Dict, FrozenSet, List, Optional, Set, Tuple

from google import genai
from google.genai import types

# ============================================================================
# Logging Configuration
# ============================================================================

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
logger = logging.getLogger(__name__)

# ============================================================================
# Constants & Namespaces
# ============================================================================

# XML namespaces
# Note: Do NOT register "xml" namespace - it's reserved and causes ValueError
XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace"
XLIFF_NAMESPACE = "urn:oasis:names:tc:xliff:document:1.2"
TOOLS_NAMESPACE = "http://schemas.android.com/tools"

# Register namespaces to preserve prefixes during serialization
ET.register_namespace("xliff", XLIFF_NAMESPACE)
ET.register_namespace("tools", TOOLS_NAMESPACE)

# Directories to exclude when searching for string files
DEFAULT_EXCLUDE_DIRS: FrozenSet[str] = frozenset({
    ".git", ".gradle", "build", ".idea", "node_modules",
    "__pycache__", "venv", ".venv", ".svn", ".hg", "target",
    "bin", "obj", ".dart_tool", ".pub-cache",
})

# Attributes to propagate from source to translated strings
PROPAGATE_ATTRIBUTES: FrozenSet[str] = frozenset({
    "formatted",
    "product",
})

# Allowed markup tags in Android string resources
ALLOWED_TAGS: FrozenSet[str] = frozenset({
    # Text formatting
    "b", "i", "u", "s", "strike", "del", "ins",
    "strong", "em", "cite", "dfn", "code", "samp", "kbd", "var",
    "big", "small", "sup", "sub", "tt",
    # Links and styling
    "a", "font", "annotation", "span",
    # XLIFF placeholders
    "xliff:g", "g",
})

# ============================================================================
# Regex Patterns
# ============================================================================

# Android/Java printf-style placeholders
# Handles: %s, %d, %1$s, %2$d, %.2f, %%, %n, %b, %h, %t* (time formats)
PLACEHOLDER_PATTERNS = [
    r"%%",  # Escaped percent (must match first)
    r"%n",  # Platform newline
    r"%(?:\d+\$)?[-+# 0,(]*\d*(?:\.\d+)?[sdbBhHoOxXeEfgGaAcC]",  # Standard
    r"%(?:\d+\$)?[-+# 0,(]*\d*(?:\.\d+)?t[HIklMSLNpzZsQBbhAaCYyjmdeRTrDFc]",  # Time
]
PLACEHOLDER_RE = re.compile("|".join(PLACEHOLDER_PATTERNS))

# XLIFF tags (must be matched before generic tags - they can span multiple lines)
XLIFF_TAG_RE = re.compile(
    r"<xliff:g[^>]*>.*?</xliff:g>|<xliff:g[^>]*/\s*>",
    re.DOTALL | re.IGNORECASE,
)

# Generic XML/HTML tags (opening, closing, self-closing)
MARKUP_TAG_RE = re.compile(r"</?[a-zA-Z][^>]*>")

# For detecting if text has any markup (same as above, used for quick checks)
MARKUP_PATTERN = re.compile(r"</?[a-zA-Z][^>]*>")

# Tag name extraction for validation
TAG_NAME_PATTERN = re.compile(r"</?([a-zA-Z][a-zA-Z0-9:]*)")

# Bare ampersands (not part of valid XML entities)
# XML only defines: amp, lt, gt, quot, apos
BARE_AMPERSAND_PATTERN = re.compile(
    r"&(?!(amp|lt|gt|quot|apos);|#\d+;|#x[0-9a-fA-F]+;)"
)

# HTML entities (case-insensitive, optional semicolon for common LLM errors)
HTML_ENTITY_PATTERN = re.compile(
    r"&(nbsp|copy|reg|trade|mdash|ndash|hellip|bull|euro|pound|yen|cent);?",
    re.IGNORECASE,
)

# HTML entity to numeric XML entity mapping
HTML_ENTITY_TO_NUMERIC: Dict[str, str] = {
    "nbsp": "&#160;",
    "copy": "&#169;",
    "reg": "&#174;",
    "trade": "&#8482;",
    "mdash": "&#8212;",
    "ndash": "&#8211;",
    "hellip": "&#8230;",
    "bull": "&#8226;",
    "euro": "&#8364;",
    "pound": "&#163;",
    "yen": "&#165;",
    "cent": "&#162;",
}

# Token sequence pattern for order validation
TOKEN_SEQUENCE_RE = re.compile(r"$$\[(?:PH|TAG)_\d+$$\]")

# ============================================================================
# Exceptions
# ============================================================================


class TranslationError(Exception):
    """Raised when translation API call fails."""
    pass


class XmlWriteError(Exception):
    """Raised when writing XML fails validation."""
    pass


class ValidationError(Exception):
    """Raised when translation validation fails."""
    pass


# ============================================================================
# Data Structures
# ============================================================================


@dataclass
class Config:
    """Application configuration."""
    repo_root: Path
    mode: str  # "check" or "apply"
    locales: List[str]
    model: str
    batch_size: int
    api_key: str
    exclude_dirs: FrozenSet[str] = DEFAULT_EXCLUDE_DIRS
    max_retries: int = 5
    base_retry_delay: float = 10.0
    request_delay: float = 2.0
    validate_output: bool = True
    warn_unknown_tags: bool = True


@dataclass
class StringEntry:
    """
    A string resource entry with text and attributes.

    Preserves source attributes that must be propagated to translations:
    - formatted: If "false", % chars are literal (critical!)
    - product: Device-specific variants (tablet, device, etc.)
    - tools:* attributes: Tooling metadata

    Note: The text is stored WITHOUT stripping whitespace, as leading/trailing
    spaces can be intentional (especially with xml:space="preserve").
    """
    key: str
    text: str
    attributes: Dict[str, str] = field(default_factory=dict)

    @property
    def is_formatted(self) -> bool:
        """
        Returns True if this string uses format placeholders.
        Returns False if formatted="false" (% is literal text).

        This is CRITICAL: if formatted="false", we must NOT freeze
        placeholder patterns like %s because they are literal text.
        """
        return self.attributes.get("formatted", "true").lower() != "false"

    @property
    def has_xml_space_preserve(self) -> bool:
        """Returns True if source has xml:space='preserve'."""
        return self.attributes.get(f"{{{XML_NAMESPACE}}}space") == "preserve"

    def get_propagated_attributes(self) -> Dict[str, str]:
        """Get attributes to copy to translated string element."""
        result = {"name": self.key}

        # Copy standard preserved attributes
        for attr in PROPAGATE_ATTRIBUTES:
            if attr in self.attributes:
                result[attr] = self.attributes[attr]

        # Copy tools:* namespace attributes
        for key, value in self.attributes.items():
            if key.startswith(f"{{{TOOLS_NAMESPACE}}}") or key.startswith("tools:"):
                result[key] = value

        # Propagate xml:space if present in source
        xml_space_key = f"{{{XML_NAMESPACE}}}space"
        if xml_space_key in self.attributes:
            result[xml_space_key] = self.attributes[xml_space_key]

        return result


@dataclass
class FrozenText:
    """
    Text with placeholders and markup tags replaced by tokens.

    This ensures the LLM cannot corrupt format specifiers or markup.
    Tokens are validated after translation to ensure:
    1. All tokens are present (no missing placeholders/tags)
    2. Token ORDER is preserved (critical for proper markup nesting)

    Example:
        Original: "Hello <b>%s</b>!"
        Frozen:   "Hello [[TAG_0]][[PH_0]][[TAG_1]]!"

    After translation and validation, tokens are restored.
    """
    original: str
    frozen: str
    placeholders: List[str]  # Original placeholder strings
    tags: List[str]          # Original tag strings

    def unfreeze(self, translated_frozen: str) -> str:
        """Restore placeholders and tags in translated text."""
        result = translated_frozen

        for i, ph in enumerate(self.placeholders):
            result = result.replace(f"[[PH_{i}]]", ph)

        for i, tag in enumerate(self.tags):
            result = result.replace(f"[[TAG_{i}]]", tag)

        return result

    def validate(self, translated_frozen: str) -> Tuple[bool, List[str]]:
        """
        Validate that all tokens are present AND in the correct order.

        This catches:
        - Missing placeholders or tags
        - Reordered tokens (which would break markup nesting)
        - Duplicated tokens
        - Extra tokens

        Returns: (is_valid, list_of_error_messages)
        """
        errors: List[str] = []

        # Check presence of each placeholder token
        for i, ph in enumerate(self.placeholders):
            token = f"[[PH_{i}]]"
            if token not in translated_frozen:
                errors.append(f"Missing placeholder {token} (was: {ph})")

        # Check presence of each tag token
        for i, tag in enumerate(self.tags):
            token = f"[[TAG_{i}]]"
            if token not in translated_frozen:
                # Truncate long tags for readability
                tag_preview = tag[:40] + "..." if len(tag) > 40 else tag
                errors.append(f"Missing tag {token} (was: {tag_preview})")

        # Check token ORDER is preserved
        # This is critical because reordering tags can break XML structure
        expected_tokens = TOKEN_SEQUENCE_RE.findall(self.frozen)
        actual_tokens = TOKEN_SEQUENCE_RE.findall(translated_frozen)

        if expected_tokens != actual_tokens:
            # Only report order error if presence checks passed
            # (otherwise missing tokens already explain the difference)
            if not errors:
                errors.append(
                    f"Token order changed: expected {expected_tokens}, "
                    f"got {actual_tokens}"
                )

        return len(errors) == 0, errors

    @property
    def has_tokens(self) -> bool:
        """Returns True if any tokens were frozen."""
        return bool(self.placeholders or self.tags)

    @property
    def token_count(self) -> int:
        """Total number of tokens."""
        return len(self.placeholders) + len(self.tags)


@dataclass
class LocaleResult:
    """Translation results for a single locale and source file."""
    locale: str
    source_path: Path
    target_path: Path
    total_source: int = 0
    already_translated: int = 0
    newly_translated: int = 0
    failed: int = 0
    errors: List[str] = field(default_factory=list)

    @property
    def missing_before(self) -> int:
        """Number of strings that were missing before processing."""
        return self.total_source - self.already_translated


@dataclass
class ProcessingResult:
    """Overall processing results across all files and locales."""
    locale_results: List[LocaleResult] = field(default_factory=list)

    @property
    def total_missing_before(self) -> int:
        """Total missing strings before processing."""
        return sum(r.missing_before for r in self.locale_results)

    @property
    def total_translated(self) -> int:
        """Total strings newly translated."""
        return sum(r.newly_translated for r in self.locale_results)

    @property
    def total_failed(self) -> int:
        """Total strings that failed translation."""
        return sum(r.failed for r in self.locale_results)

    @property
    def has_missing(self) -> bool:
        """True if there are still missing translations."""
        return (self.total_missing_before - self.total_translated) > 0

    @property
    def has_failures(self) -> bool:
        """True if any translations failed."""
        return self.total_failed > 0


# ============================================================================
# Text Freezing Functions
# ============================================================================


def freeze_text(text: str, freeze_placeholders: bool = True) -> FrozenText:
    """
    Replace placeholders and markup tags with tokens for safe translation.

    Args:
        text: Original string text (whitespace preserved)
        freeze_placeholders: If False, skip placeholder freezing.
                            Use False for strings with formatted="false".

    Returns:
        FrozenText with tokens replacing sensitive content

    The freezing order is important:
    1. XLIFF tags first (they may contain nested content)
    2. Other markup tags
    3. Printf placeholders (only if freeze_placeholders=True)
    """
    frozen = text
    placeholders: List[str] = []
    tags: List[str] = []

    # Step 1: Freeze XLIFF tags first (they may contain other elements)
    def freeze_xliff(match: re.Match) -> str:
        tags.append(match.group(0))
        return f"[[TAG_{len(tags) - 1}]]"

    frozen = XLIFF_TAG_RE.sub(freeze_xliff, frozen)

    # Step 2: Freeze remaining markup tags
    def freeze_tag(match: re.Match) -> str:
        tags.append(match.group(0))
        return f"[[TAG_{len(tags) - 1}]]"

    frozen = MARKUP_TAG_RE.sub(freeze_tag, frozen)

    # Step 3: Freeze placeholders (only if string uses formatting)
    if freeze_placeholders:
        def freeze_ph(match: re.Match) -> str:
            placeholders.append(match.group(0))
            return f"[[PH_{len(placeholders) - 1}]]"

        frozen = PLACEHOLDER_RE.sub(freeze_ph, frozen)

    return FrozenText(
        original=text,
        frozen=frozen,
        placeholders=placeholders,
        tags=tags,
    )


# ============================================================================
# Text Sanitization Functions
# ============================================================================


def convert_html_entities_to_numeric(text: str) -> str:
    """
    Convert HTML named entities to XML numeric entities.

    Handles:
    - Case variations: &nbsp; &NBSP; &Nbsp;
    - Missing semicolons: &nbsp (common LLM error)

    XML only recognizes 5 named entities (amp, lt, gt, quot, apos).
    All others must be converted to numeric form for valid XML.
    """
    def replace_entity(match: re.Match) -> str:
        name = match.group(1).lower()
        return HTML_ENTITY_TO_NUMERIC.get(name, match.group(0))

    return HTML_ENTITY_PATTERN.sub(replace_entity, text)


def fix_bare_ampersands(text: str) -> str:
    """
    Replace bare ampersands with &amp; for XML validity.

    Preserves valid XML entities:
    - Named: &amp; &lt; &gt; &quot; &apos;
    - Decimal: &#123;
    - Hexadecimal: &#xABCD;
    """
    return BARE_AMPERSAND_PATTERN.sub("&amp;", text)


def sanitize_for_xml_parse(text: str) -> str:
    """
    Prepare text for XML parsing.

    Order matters:
    1. Convert HTML entities to numeric (so &nbsp; becomes &#160;)
    2. Escape bare ampersands (so "A & B" becomes "A &amp; B")
    """
    result = convert_html_entities_to_numeric(text)
    return fix_bare_ampersands(result)


def escape_android_string(text: str) -> str:
    """
    Escape Android special characters in string resources.

    Rules:
    - ' (apostrophe) → \\' (required unless string is double-quoted)
    - @ at start → \\@ (prevents resource reference interpretation)
    - ? at start → \\? (prevents theme attribute interpretation)

    Preserves existing escape sequences to avoid double-escaping.

    Note: Ampersands are NOT escaped here — ElementTree handles that
    automatically during serialization for text content.
    """
    if not text:
        return text

    result: List[str] = []
    i = 0
    length = len(text)

    while i < length:
        char = text[i]

        # Detect and preserve existing escape sequences
        if char == '\\' and i + 1 < length:
            next_char = text[i + 1]

            # Standard Android/Java escape sequences
            if next_char in ("'", '"', '\\', 'n', 't', 'r', '@', '?'):
                result.append(char)
                result.append(next_char)
                i += 2
                continue

            # Unicode escape: \uXXXX
            if next_char == 'u' and i + 5 <= length:
                hex_chars = text[i + 2:i + 6]
                if len(hex_chars) == 4 and all(
                    c in '0123456789abcdefABCDEF' for c in hex_chars
                ):
                    result.append(text[i:i + 6])
                    i += 6
                    continue

        # Apply escaping rules for unescaped characters
        if char == "'":
            result.append("\\'")
        elif char == '@' and i == 0:
            result.append('\\@')
        elif char == '?' and i == 0:
            result.append('\\?')
        else:
            result.append(char)

        i += 1

    return ''.join(result)


def escape_android_text_nodes(element: ET.Element) -> None:
    """
    Recursively escape Android special characters in text and tail content.

    Only modifies text content — never attributes or tag names.
    """
    if element.text:
        element.text = escape_android_string(element.text)

    for child in element:
        escape_android_text_nodes(child)
        if child.tail:
            child.tail = escape_android_string(child.tail)


def validate_allowed_tags(value: str) -> Tuple[bool, List[str]]:
    """
    Check if all markup tags in value are in the allowlist.

    Returns: (is_valid, list_of_unknown_tags)
    """
    if not MARKUP_PATTERN.search(value):
        return True, []

    found = set(TAG_NAME_PATTERN.findall(value))
    unknown = [t for t in found if t.lower() not in ALLOWED_TAGS]
    return len(unknown) == 0, unknown


# ============================================================================
# XML Reading Functions
# ============================================================================


def get_element_full_text(elem: ET.Element) -> str:
    """
    Get full text content including child elements as markup.

    Handles mixed content like: "Hello <b>World</b>!"
    Returns: "Hello <b>World</b>!"

    Note: Does NOT strip whitespace - caller must preserve it.
    """
    parts: List[str] = []

    if elem.text:
        parts.append(elem.text)

    for child in elem:
        parts.append(ET.tostring(child, encoding="unicode"))
        if child.tail:
            parts.append(child.tail)

    return "".join(parts)


def read_source_strings(source_xml: Path) -> List[StringEntry]:
    """
    Read translatable strings from source XML, preserving attributes.

    IMPORTANT: Text content is NOT stripped. Leading/trailing whitespace
    is preserved as it may be intentional (especially with xml:space="preserve").

    Skips:
    - Strings with translatable="false"
    - Empty strings (after checking, not stripping)
    - String arrays and plurals (not handled by this script)

    Preserves:
    - formatted attribute (critical for % handling)
    - product attribute (device variants)
    - xml:space attribute (whitespace preservation)
    - tools:* attributes (tooling metadata)
    """
    tree = ET.parse(source_xml)
    root = tree.getroot()
    entries: List[StringEntry] = []

    for node in root.findall("string"):
        name = node.attrib.get("name")
        if not name:
            continue

        # Skip non-translatable strings
        if node.attrib.get("translatable", "true").lower() == "false":
            continue

        # Get full text content WITHOUT stripping
        raw_text = get_element_full_text(node)

        # Skip empty strings (check stripped version, but keep original)
        if not raw_text or not raw_text.strip():
            continue

        # Use original text with whitespace preserved
        text = raw_text

        # Collect attributes to preserve
        preserved: Dict[str, str] = {}
        for attr_key, attr_val in node.attrib.items():
            if attr_key in ("name", "translatable"):
                continue

            # Preserve standard attributes
            if attr_key in PROPAGATE_ATTRIBUTES:
                preserved[attr_key] = attr_val

            # Preserve xml:space attribute
            elif attr_key == f"{{{XML_NAMESPACE}}}space":
                preserved[attr_key] = attr_val

            # Preserve tools:* namespace attributes
            elif attr_key.startswith(f"{{{TOOLS_NAMESPACE}}}"):
                preserved[attr_key] = attr_val

        entries.append(StringEntry(key=name, text=text, attributes=preserved))

    return entries


def read_existing_keys(target_xml: Path) -> Set[str]:
    """Read existing string keys from target file."""
    if not target_xml.exists():
        return set()

    try:
        tree = ET.parse(target_xml)
        return {
            node.attrib.get("name")
            for node in tree.getroot().findall("string")
            if node.attrib.get("name")
        }
    except ET.ParseError:
        return set()


# ============================================================================
# XML Writing Functions
# ============================================================================


def set_mixed_string_value(
    node: ET.Element,
    value: str,
    key: Optional[str] = None,
    warn_unknown_tags: bool = True,
) -> None:
    """
    Set string node value, preserving embedded markup.

    Handles:
    - Plain text: "Hello World"
    - HTML formatting: "Hello <b>World</b>!"
    - XLIFF placeholders: "Balance: <xliff:g id='amt'>%1$s</xliff:g>"
    - Mixed content with entities and ampersands

    Falls back to plain text (markup escaped) if parsing fails.
    """
    # Clear any existing content
    node.text = None
    for child in list(node):
        node.remove(child)

    key_prefix = f"[{key}] " if key else ""

    # Warn about unknown tags
    if warn_unknown_tags and MARKUP_PATTERN.search(value):
        is_valid, unknown = validate_allowed_tags(value)
        if not is_valid:
            logger.warning(f"{key_prefix}Unknown tags (may not render): {unknown}")

    # Fast path: no markup, treat as plain text
    if not MARKUP_PATTERN.search(value):
        converted = convert_html_entities_to_numeric(value)
        node.text = escape_android_string(converted)
        return

    # Has markup: sanitize first, then parse
    sanitized = sanitize_for_xml_parse(value)
    wrapped = f"<_root xmlns:xliff='{XLIFF_NAMESPACE}'>{sanitized}</_root>"

    try:
        fragment = ET.fromstring(wrapped)
    except ET.ParseError as e:
        logger.warning(f"{key_prefix}XML parse failed, using plain text: {e}")
        # Fallback: still convert HTML entities so &nbsp; renders correctly
        fallback = convert_html_entities_to_numeric(value)
        node.text = escape_android_string(fallback)
        return

    # Transfer content from fragment to target node
    node.text = fragment.text

    # Explicitly detach children before appending (safe across Python versions)
    for child in list(fragment):
        fragment.remove(child)
        node.append(child)

    # Escape Android special chars in text nodes only
    escape_android_text_nodes(node)


def indent_xml(elem: ET.Element, level: int = 0) -> None:
    """Add pretty-print indentation to XML."""
    indent = "\n" + "    " * level

    if len(elem):  # Has children
        if not elem.text or not elem.text.strip():
            elem.text = indent + "    "
        if not elem.tail or not elem.tail.strip():
            elem.tail = indent

        last_child: Optional[ET.Element] = None
        for child in elem:
            indent_xml(child, level + 1)
            last_child = child

        if last_child is not None and (not last_child.tail or not last_child.tail.strip()):
            last_child.tail = indent
    elif level and (not elem.tail or not elem.tail.strip()):
        elem.tail = indent


def write_translations(
    target_xml: Path,
    translations: Dict[str, str],
    source_entries: List[StringEntry],
    validate: bool = True,
    warn_unknown_tags: bool = True,
) -> int:
    """
    Write translations to target XML file.

    Features:
    - Appends to existing file or creates new one
    - Preserves embedded markup (HTML, xliff tags)
    - Propagates source attributes (formatted, product, xml:space, etc.)
    - Adds xml:space="preserve" for significant whitespace
    - Validates written file can be parsed

    Returns: Number of strings written
    Raises: XmlWriteError if validation fails
    """
    target_xml.parent.mkdir(parents=True, exist_ok=True)

    # Load existing file or create new structure
    if target_xml.exists():
        try:
            tree = ET.parse(target_xml)
            root = tree.getroot()
        except ET.ParseError as e:
            logger.warning(f"Corrupted file '{target_xml}', recreating: {e}")
            root = ET.Element("resources")
            tree = ET.ElementTree(root)
    else:
        root = ET.Element("resources")
        tree = ET.ElementTree(root)

    # Collect existing keys to avoid duplicates
    existing: Set[str] = {
        n.attrib.get("name")
        for n in root.findall("string")
        if n.attrib.get("name")
    }

    # Add new translations in source order
    written = 0
    for entry in source_entries:
        if entry.key in existing:
            continue
        if entry.key not in translations:
            continue

        value = translations[entry.key]

        # Create node with propagated attributes from source
        attrs = entry.get_propagated_attributes()
        node = ET.SubElement(root, "string", attrs)

        # Add xml:space="preserve" if value has significant whitespace
        # and source didn't already have it (it would be propagated)
        xml_space_key = f"{{{XML_NAMESPACE}}}space"
        if xml_space_key not in attrs:
            if value and (value[0].isspace() or value[-1].isspace()):
                node.set(xml_space_key, "preserve")

        set_mixed_string_value(
            node, value,
            key=entry.key,
            warn_unknown_tags=warn_unknown_tags,
        )
        written += 1

    # Write file if changes were made
    if written > 0:
        indent_xml(root)
        tree.write(target_xml, encoding="utf-8", xml_declaration=True)

        # Validate written file can be parsed
        if validate:
            try:
                ET.parse(target_xml)
            except ET.ParseError as e:
                raise XmlWriteError(f"Written file is malformed: {target_xml}: {e}")

    return written


# ============================================================================
# File Discovery
# ============================================================================


def find_source_files(repo_root: Path, exclude_dirs: FrozenSet[str]) -> List[Path]:
    paths: List[Path] = []

    patterns = [
        "src/*/res/values/strings.xml",                 # Android (main, debug, flavors, androidMain, etc.)
        "src/*/composeResources/values/strings.xml",    # Compose Multiplatform resources
    ]

    for pat in patterns:
        for p in repo_root.rglob(pat):
            if any(part in exclude_dirs for part in p.parts):
                continue

            # Skip tests
            if "src" in p.parts:
                src_set = p.parts[p.parts.index("src") + 1]
                if src_set in ("test", "androidTest"):
                    continue

            paths.append(p)

    # Deduplicate + stable order
    return sorted(set(paths))


def get_target_path(source_xml: Path, locale: str) -> Path:
    """
    Works for:
      - .../res/values/strings.xml
      - .../composeResources/values/strings.xml
    """
    values_dir = source_xml.parent  # the "values" folder
    parent = values_dir.parent      # res/ or composeResources/
    return parent / f"values-{locale}" / "strings.xml"


def get_module_name(source_path: Path) -> str:
    """
    Extract module name from source strings.xml path.

    Expected: .../<module>/src/<variant>/res/values/strings.xml
    Returns: <module> name

    Handles various Android project structures:
    - Standard: module/src/main/res/values/strings.xml
    - Flavor: module/src/flavorDebug/res/values/strings.xml
    - Library: libraries/mylib/src/main/res/values/strings.xml
    - Nested: features/auth/src/main/res/values/strings.xml
    """
    parts = source_path.parts

    # Find all occurrences of 'src' and use the first one
    src_indexes = [i for i, p in enumerate(parts) if p == "src"]

    if src_indexes:
        first_src_index = src_indexes[0]
        if first_src_index > 0:
            return parts[first_src_index - 1]

    # Fallback for non-standard paths
    if len(source_path.parents) > 4:
        return source_path.parents[4].name

    return "unknown"


# ============================================================================
# Translation API
# ============================================================================


class GeminiTranslator:
    """Translator using Google Gemini API with retry and rate limiting."""

    # System prompt with comprehensive instructions
    SYSTEM_PROMPT = """You are a professional translator for a mobile banking/microfinance Android app.
Translate UI strings accurately and naturally for the target locale.

CRITICAL RULES — FOLLOW EXACTLY:

1. OUTPUT FORMAT:
   - Return ONLY a valid JSON object mapping keys to translated strings.
   - Do NOT include markdown, code blocks, backticks, or any commentary.
   - Do NOT wrap the response in ```json``` or any other formatting.

2. PLACEHOLDER TOKENS (e.g., [[PH_0]], [[PH_1]], [[PH_2]]):
   - These represent format placeholders like %s, %d, %1$s.
   - Preserve ALL placeholder tokens EXACTLY as written.
   - Do NOT translate, modify, reorder, or remove any [[PH_N]] tokens.
   - Keep tokens in the SAME ORDER as the original.

3. TAG TOKENS (e.g., [[TAG_0]], [[TAG_1]], [[TAG_2]]):
   - These represent XML/HTML markup like <b>, </b>, <xliff:g>.
   - Preserve ALL tag tokens EXACTLY as written.
   - Do NOT translate, modify, reorder, or remove any [[TAG_N]] tokens.
   - Keep tokens in the SAME ORDER as the original (critical for proper nesting).
   - Opening and closing tag pairs must stay together properly nested.

4. TOKEN ORDER IS CRITICAL:
   - The order of [[PH_N]] and [[TAG_N]] tokens must be preserved exactly.
   - Changing token order will break the application.
   - Example: [[TAG_0]]text[[TAG_1]] must stay in that order.

5. KEYS:
   - Do NOT add or remove keys from the input.
   - Every input key must appear exactly once in the output.

6. TRANSLATION QUALITY:
   - Translate naturally for the target locale.
   - Use standard banking/financial terminology for the locale.
   - Respect cultural conventions (number formats, politeness levels, etc.).
   - Preserve the tone and formality of the original.

7. WHITESPACE:
   - Preserve leading and trailing spaces if present in the original.
   - These spaces are often intentional for formatting.

8. SPECIAL CHARACTERS:
   - Preserve any special punctuation around tokens.
   - Maintain spacing around tokens as appropriate for the target language.

Example input:
{"items": [{"key": "welcome", "text": "Hello [[TAG_0]][[PH_0]][[TAG_1]], your balance is [[PH_1]]"}]}

Example output (Spanish):
{"welcome": "Hola [[TAG_0]][[PH_0]][[TAG_1]], tu saldo es [[PH_1]]"}"""

    def __init__(self, config: Config):
        self.config = config
        self.client = genai.Client(api_key=config.api_key)
        self._last_request_time = 0.0

    def _rate_limit(self) -> None:
        """Enforce minimum delay between API requests."""
        elapsed = time.time() - self._last_request_time
        if elapsed < self.config.request_delay:
            time.sleep(self.config.request_delay - elapsed)
        self._last_request_time = time.time()

    def translate_batch(
        self,
        locale: str,
        items: List[Tuple[str, FrozenText]],
    ) -> Dict[str, str]:
        """
        Translate a batch of frozen texts.
        Handles both Gemini (with system instructions) and Gemma (without) models.
        """
        self._rate_limit()

        # Build request payload
        payload = {
            "target_locale": locale,
            "items": [{"key": k, "text": ft.frozen} for k, ft in items],
        }

        # Check if model supports system instructions
        model_lower = self.config.model.lower()
        supports_system_instruction = not any(x in model_lower for x in ["gemma", "embedding", "aqa"])

        if supports_system_instruction:
            # For Gemini models: use system instruction
            user_prompt = (
                f"Translate all items from English to '{locale}'.\n"
                f"Return a JSON object mapping each key to its translation.\n"
                f"Preserve all [[PH_N]] and [[TAG_N]] tokens in the SAME ORDER.\n\n"
                f"Input:\n{json.dumps(payload, ensure_ascii=False, indent=2)}"
            )

            gen_config = types.GenerateContentConfig(
                system_instruction=self.SYSTEM_PROMPT,
                temperature=0.1,
                response_mime_type="application/json",
                max_output_tokens=4096,
            )
        else:
            # For Gemma models: include instructions in the user prompt
            user_prompt = f"""{self.SYSTEM_PROMPT}

    ---

    Now translate all items from English to '{locale}'.
    Return a JSON object mapping each key to its translation.
    Preserve all [[PH_N]] and [[TAG_N]] tokens in the SAME ORDER.

    Input:
    {json.dumps(payload, ensure_ascii=False, indent=2)}

    Output (JSON only, no markdown):"""

            gen_config = types.GenerateContentConfig(
                temperature=0.1,
                # Note: Gemma might not support response_mime_type either
                max_output_tokens=4096,
            )

        last_error: Optional[Exception] = None
        requested_keys = {k for k, _ in items}

        for attempt in range(1, self.config.max_retries + 1):
            try:
                response = self.client.models.generate_content(
                    model=self.config.model,
                    contents=user_prompt,
                    config=gen_config,
                )

                text = (response.text or "").strip()

                # Clean up response if it contains markdown code blocks
                if text.startswith("```"):
                    # Remove markdown code block wrapper
                    lines = text.split("\n")
                    if lines[0].startswith("```"):
                        lines = lines[1:]
                    if lines and lines[-1].strip() == "```":
                        lines = lines[:-1]
                    text = "\n".join(lines).strip()

                data = json.loads(text)

                if not isinstance(data, dict):
                    raise TranslationError("Response is not a JSON object")

                # Validate returned keys
                returned_keys = set(data.keys())
                extra_keys = returned_keys - requested_keys
                missing_keys = requested_keys - returned_keys

                if extra_keys:
                    extra_preview = sorted(extra_keys)[:5]
                    suffix = "..." if len(extra_keys) > 5 else ""
                    logger.warning(
                        f"Gemini returned unexpected keys (ignored): {extra_preview}{suffix}"
                    )

                if missing_keys:
                    logger.warning(
                        f"Gemini omitted {len(missing_keys)} key(s); will retry individually"
                    )

                # Return only requested keys
                return {str(k): str(v) for k, v in data.items() if k in requested_keys}

            except json.JSONDecodeError as e:
                last_error = TranslationError(f"Invalid JSON response: {e}")
            except Exception as e:
                last_error = e
                error_str = str(e)

                # Handle rate limiting (429 errors)
                if "429" in error_str or "RESOURCE_EXHAUSTED" in error_str:
                    # Extract retry delay from error if available
                    import re as re_module
                    match = re_module.search(r"retry in (\d+(?:\.\d+)?)", error_str, re_module.IGNORECASE)
                    delay = float(match.group(1)) + 5 if match else 60.0
                    logger.warning(f"Rate limited! Waiting {delay:.0f}s...")
                    time.sleep(delay)
                    continue

            # Exponential backoff for other errors
            delay = self.config.base_retry_delay * (2 ** (attempt - 1))
            logger.warning(
                f"Attempt {attempt}/{self.config.max_retries} failed: {last_error}. "
                f"Retrying in {delay:.1f}s..."
            )
            time.sleep(delay)

        raise TranslationError(
            f"Failed after {self.config.max_retries} attempts: {last_error}"
        )

    def translate_single(
        self,
        locale: str,
        key: str,
        frozen_text: FrozenText,
    ) -> Optional[str]:
        """
        Translate a single string.

        More reliable than batch for problematic strings.
        Returns None on failure instead of raising.
        """
        try:
            result = self.translate_batch(locale, [(key, frozen_text)])
            return result.get(key)
        except TranslationError as e:
            logger.error(f"Failed to translate '{key}': {e}")
            return None


# ============================================================================
# Main Processing Logic
# ============================================================================


def create_batches(
    items: List[Tuple[str, FrozenText]],
    batch_size: int,
) -> List[List[Tuple[str, FrozenText]]]:
    """Split items into batches of specified size."""
    return [items[i:i + batch_size] for i in range(0, len(items), batch_size)]


def process_locale(
    source_xml: Path,
    locale: str,
    config: Config,
    translator: Optional[GeminiTranslator],
) -> LocaleResult:
    """
    Process translations for a single source file and locale.

    Steps:
    1. Read source strings (with whitespace preserved)
    2. Find missing translations
    3. Freeze placeholders and tags
    4. Translate in batches
    5. Validate tokens (presence AND order)
    6. Unfreeze and write to target file
    """
    target_xml = get_target_path(source_xml, locale)

    result = LocaleResult(
        locale=locale,
        source_path=source_xml,
        target_path=target_xml,
    )

    # Read source strings with attributes (whitespace preserved)
    source_entries = read_source_strings(source_xml)
    result.total_source = len(source_entries)

    if not source_entries:
        logger.warning(f"No translatable strings in {source_xml}")
        return result

    # Find what's already translated
    existing_keys = read_existing_keys(target_xml)
    result.already_translated = len(existing_keys & {e.key for e in source_entries})

    # Find missing translations
    missing_entries = [e for e in source_entries if e.key not in existing_keys]

    if not missing_entries:
        logger.info(f"  [{locale}] All {result.total_source} strings already translated")
        return result

    logger.info(
        f"  [{locale}] {len(missing_entries)} of {result.total_source} "
        f"strings need translation → {target_xml.name}"
    )

    # Check mode - don't translate in check mode
    if config.mode == "check" or translator is None:
        return result

    # Freeze placeholders and tags for translation
    # CRITICAL: Don't freeze placeholders for formatted="false" strings
    frozen_map: Dict[str, FrozenText] = {}

    for entry in missing_entries:
        if entry.is_formatted:
            # Normal string: freeze both placeholders and tags
            frozen_map[entry.key] = freeze_text(entry.text, freeze_placeholders=True)
        else:
            # formatted="false": freeze only tags, NOT placeholders
            # because % characters are literal in these strings
            frozen_map[entry.key] = freeze_text(entry.text, freeze_placeholders=False)
            logger.debug(f"    [{entry.key}] formatted=false, preserving literal %")

    # Translate in batches
    translations: Dict[str, str] = {}
    items = [(e.key, frozen_map[e.key]) for e in missing_entries]
    batches = create_batches(items, config.batch_size)

    for batch_idx, batch in enumerate(batches, 1):
        logger.info(f"    Batch {batch_idx}/{len(batches)} ({len(batch)} strings)")

        # Try batch translation
        try:
            batch_result = translator.translate_batch(locale, batch)
        except TranslationError as e:
            logger.error(f"    Batch failed: {e}")
            result.errors.append(f"Batch {batch_idx} failed: {e}")
            batch_result = {}

        # Process each item in the batch
        for key, frozen_text in batch:
            translated_frozen: Optional[str] = batch_result.get(key)

            # If missing from batch result, try individual translation
            if translated_frozen is None:
                logger.warning(f"    [{key}] Missing from batch, retrying individually")
                translated_frozen = translator.translate_single(locale, key, frozen_text)

            # Handle complete failure
            if translated_frozen is None:
                logger.error(f"    [{key}] Translation failed")
                result.failed += 1
                result.errors.append(f"Translation failed for '{key}'")
                continue

            # Validate that all tokens are present AND in correct order
            is_valid, errors = frozen_text.validate(translated_frozen)

            if not is_valid:
                logger.warning(f"    [{key}] Validation failed: {errors}")

                # Retry individually - sometimes single-string prompts work better
                retry_result = translator.translate_single(locale, key, frozen_text)
                if retry_result:
                    is_valid_retry, errors_retry = frozen_text.validate(retry_result)
                    if is_valid_retry:
                        translated_frozen = retry_result
                        is_valid = True
                        logger.info(f"    [{key}] Retry succeeded")
                    else:
                        errors = errors_retry  # Use retry errors for logging

            # Store valid translation or log failure
            if is_valid:
                # Unfreeze tokens and store final translation
                translations[key] = frozen_text.unfreeze(translated_frozen)
            else:
                logger.error(f"    [{key}] Skipped - validation failed: {errors}")
                result.failed += 1
                result.errors.append(f"Validation failed for '{key}': {errors}")

    # Write successful translations to file
    if translations:
        try:
            written = write_translations(
                target_xml=target_xml,
                translations=translations,
                source_entries=missing_entries,
                validate=config.validate_output,
                warn_unknown_tags=config.warn_unknown_tags,
            )
            result.newly_translated = written
            logger.info(f"  [{locale}] Wrote {written} translations")
        except XmlWriteError as e:
            logger.error(f"  [{locale}] Write failed: {e}")
            result.errors.append(str(e))

    return result


def process_all(config: Config) -> ProcessingResult:
    """
    Process all source files for all configured locales.

    Returns aggregated results across all files and locales.
    """
    result = ProcessingResult()

    # Find all source files
    sources = find_source_files(config.repo_root, config.exclude_dirs)

    if not sources:
        logger.error("No source strings.xml files found")
        return result

    logger.info(f"Found {len(sources)} source file(s)")

    # Initialize translator (only for apply mode)
    translator: Optional[GeminiTranslator] = None
    if config.mode == "apply":
        translator = GeminiTranslator(config)

    # Process each source file for each locale
    for source_xml in sources:
        logger.info(f"\nProcessing: {source_xml}")

        for locale in config.locales:
            locale_result = process_locale(source_xml, locale, config, translator)
            result.locale_results.append(locale_result)

    return result


# ============================================================================
# Summary Output
# ============================================================================


def print_summary(result: ProcessingResult, mode: str) -> None:
    """Print formatted processing summary."""
    print("\n" + "=" * 70)
    print("SUMMARY")
    print("=" * 70)

    if not result.locale_results:
        print("\n  No results to display.")
        print("=" * 70)
        return

    # Group results by source file
    by_source: Dict[Path, List[LocaleResult]] = {}
    for lr in result.locale_results:
        by_source.setdefault(lr.source_path, []).append(lr)

    # Print per-module, per-locale results
    for source_path, locale_results in sorted(by_source.items()):
        module_name = get_module_name(source_path)
        print(f"\n  {module_name}/")

        for lr in sorted(locale_results, key=lambda x: x.locale):
            remaining = lr.missing_before - lr.newly_translated

            # Status indicator
            if remaining == 0 and lr.failed == 0:
                status = "✓"  # Complete
            elif lr.failed > 0:
                status = "✗"  # Has failures
            elif lr.newly_translated > 0:
                status = "◐"  # Partial progress
            else:
                status = "○"  # No action (check mode)

            translated_total = lr.already_translated + lr.newly_translated

            print(
                f"    [{lr.locale:5}] "
                f"{translated_total:3}/{lr.total_source:3} translated "
                f"(+{lr.newly_translated:2} new, {lr.failed:2} failed) {status}"
            )

    # Print totals
    print("\n" + "-" * 70)

    total_before = result.total_missing_before
    total_done = result.total_translated
    total_fail = result.total_failed
    remaining = total_before - total_done

    print(f"  Strings missing (before):   {total_before:5}")
    print(f"  Newly translated:           {total_done:5}")
    print(f"  Failed:                     {total_fail:5}")
    print(f"  Still missing:              {remaining:5}")

    print("=" * 70)


# ============================================================================
# CLI
# ============================================================================


def parse_args() -> argparse.Namespace:
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(
        description="Translate Android string resources using Gemini API",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Check for missing translations (CI dry-run)
  %(prog)s --mode check --locales es,de,fr

  # Apply translations
  %(prog)s --mode apply --locales es,de,fr

  # Custom settings
  %(prog)s --mode apply --locales es --batch-size 15 --model gemini-1.5-pro

  # Verbose output for debugging
  %(prog)s --mode apply --locales es --verbose

Environment Variables:
  GEMINI_API_KEY    API key for Google Gemini (required for apply mode)

CI Integration:
  After running translations, validate with Android build:
    ./gradlew assembleDebug

  Or at minimum:
    ./gradlew processDebugResources

  This catches AAPT2 errors that XML parsing alone cannot detect,
  such as format string mismatches and invalid escape sequences.

Exit Codes:
  0 - Success (check: all present, apply: all translated)
  1 - Failure (apply: some translations failed)
  2 - Missing (check: missing translations detected)
""",
    )

    parser.add_argument(
        "--repo-root",
        type=Path,
        default=Path("."),
        help="Repository root directory (default: current directory)",
    )
    parser.add_argument(
        "--mode",
        choices=["check", "apply"],
        required=True,
        help="'check' reports missing translations; 'apply' translates them",
    )
    parser.add_argument(
        "--locales",
        default="es,de",
        help="Comma-separated target locales (default: es,de)",
    )
    parser.add_argument(
        "--model",
        default="gemini-2.0-flash",
        help="Gemini model to use (default: gemini-2.0-flash)",
    )
    parser.add_argument(
        "--batch-size",
        type=int,
        default=20,
        help="Strings per API call (default: 20)",
    )
    parser.add_argument(
        "--api-key-env",
        default="GEMINI_API_KEY",
        help="Environment variable for API key (default: GEMINI_API_KEY)",
    )
    parser.add_argument(
        "--no-validate",
        action="store_true",
        help="Skip post-write XML validation",
    )
    parser.add_argument(
        "--verbose", "-v",
        action="store_true",
        help="Enable debug logging",
    )
    parser.add_argument(
        "--request-delay",
        type=float,
        default=2.0,
        help="Delay between API requests in seconds (default: 2.0)",
    )

    return parser.parse_args()


def main() -> int:
    """Main entry point. Returns exit code."""
    args = parse_args()

    # Configure logging level
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)

    # Parse and validate locales
    locales = [loc.strip() for loc in args.locales.split(",") if loc.strip()]
    if not locales:
        logger.error("No locales specified")
        return 1

    # Get API key for apply mode
    api_key = ""
    if args.mode == "apply":
        api_key = os.environ.get(args.api_key_env, "").strip()
        if not api_key:
            logger.error(
                f"API key required for apply mode. "
                f"Set {args.api_key_env} environment variable."
            )
            return 1

    # Build configuration
    config = Config(
        repo_root=args.repo_root.resolve(),
        mode=args.mode,
        locales=locales,
        model=args.model,
        batch_size=args.batch_size,
        api_key=api_key,
        validate_output=not args.no_validate,
        request_delay=args.request_delay
    )

    # Log configuration
    logger.info(f"Mode: {config.mode}")
    logger.info(f"Locales: {', '.join(config.locales)}")
    logger.info(f"Repository: {config.repo_root}")
    if config.mode == "apply":
        logger.info(f"Model: {config.model}")
        logger.info(f"Batch size: {config.batch_size}")

    # Run processing
    result = process_all(config)

    # Print summary
    print_summary(result, config.mode)

    # Determine exit code
    if config.mode == "check":
        if result.total_missing_before > 0:
            logger.warning(
                f"\n⚠ Missing translations detected: "
                f"{result.total_missing_before} string(s)"
            )
            return 2
        logger.info("\n✓ All translations present.")
        return 0

    # Apply mode exit codes
    if result.has_failures:
        logger.warning(
            f"\n⚠ {result.total_failed} translation(s) failed. "
            f"Review logs for details."
        )
        return 1

    if result.total_translated > 0:
        logger.info(f"\n✓ Successfully translated {result.total_translated} string(s).")
        logger.info("  Validate with: ./gradlew assembleDebug")
    else:
        logger.info("\n✓ No new translations needed.")

    return 0


if __name__ == "__main__":
    sys.exit(main())
