package org.example.project.design_system.color

import androidx.compose.ui.graphics.Color

/**
 * Defines the set of semantic colors for the Dark theme.
 */
val darkTextColors = MedAiTextColors(
    primary = neutral100,
    secondary = neutral400,
    tertiary = neutral600,
    onPrimary = baseBlack,
    onBackground = neutral100
)

val darkStatusColors = MedAiStatusColors(
    success = success,
    warning = warning,
    error = error,
    successContainer = Color(0xFF064E3B),
    warningContainer = Color(0xFF78350F),
    errorContainer = Color(0xFF7F1D1D),
    starRating = gold,
    waiting = warningAlt,
    completed = successAlt,
    cancelled = errorAlt
)

val darkColors = MedAiColors(
    primary = primary500,
    secondary = secondary400,
    accent = accent,
    background = baseBlack,
    surface = secondary900,
    onPrimary = baseWhite,
    onSecondary = baseWhite,
    onBackground = baseWhite,
    onSurface = baseWhite,
    neutral = neutral400,
    status = darkStatusColors,
    text = darkTextColors
)
