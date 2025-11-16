package org.example.project.design_system.color

/**
 * Defines the set of semantic colors for the Light theme.
 */
val lightTextColors = MedAiTextColors(
    primary = baseBlack,
    secondary = neutral700,
    tertiary = neutral500,
    onPrimary = baseWhite,
    onBackground = baseBlack
)

val lightStatusColors = MedAiStatusColors(
    success = success200,
    warning = warning200,
    error = error200,
    successContainer = success100,
    warningContainer = warning100,
    errorContainer = error100
)

val lightColors = MedAiColors(
    // Core
    primary = primary,
    secondary = secondary,
    background = baseWhite,
    surface = baseWhite,
    onPrimary = baseWhite,
    onSecondary = baseWhite,
    onBackground = baseBlack,
    onSurface = baseBlack,

    // Neutrals
    neutral = neutral500,

    // Status & Text
    status = lightStatusColors,
    text = lightTextColors
)
