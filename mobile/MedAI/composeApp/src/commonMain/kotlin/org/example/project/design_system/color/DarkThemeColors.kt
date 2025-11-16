package org.example.project.design_system.color

/**
 * Defines the set of semantic colors for the Dark theme.
 */
val darkTextColors = MedAiTextColors(
    primary = baseWhite,
    secondary = neutral100,
    tertiary = neutral400,
    onPrimary = baseBlack,
    onBackground = baseWhite
)

val darkStatusColors = MedAiStatusColors(
    success = success100,
    warning = warning100,
    error = error100,
    successContainer = success300,
    warningContainer = warning300,
    errorContainer = error300
)

val darkColors = MedAiColors(
    // Core
    primary = primary300,
    secondary = secondary400,
    background = baseBlack,
    surface = neutral900,
    onPrimary = primary900,
    onSecondary = secondary900,
    onBackground = baseWhite,
    onSurface = baseWhite,

    // Neutrals
    neutral = neutral500,

    // Status & Text
    status = darkStatusColors,
    text = darkTextColors
)
