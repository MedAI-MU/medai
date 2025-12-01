package org.example.project.design_system.color

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
    success = success300,
    warning = warning300,
    error = error300,
    successContainer = success100.copy(alpha = 0.1f),
    warningContainer = warning100.copy(alpha = 0.1f),
    errorContainer = error100.copy(alpha = 0.1f)
)

val darkColors = MedAiColors(
    primary = primary400, // Lighter blue for dark mode visibility
    secondary = secondary400,
    background = baseBlack, // Slate 950
    surface = secondary900, // Slate 900
    onPrimary = baseBlack,
    onSecondary = baseBlack,
    onBackground = neutral100,
    onSurface = neutral100,
    neutral = neutral600,
    status = darkStatusColors,
    text = darkTextColors
)
