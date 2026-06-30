package org.example.project.design_system.color

/**
 * Defines the set of semantic colors for the Light theme.
 */
val lightTextColors = MedAiTextColors(
    primary = baseBlack,
    secondary = secondary600,
    tertiary = secondary400,
    onPrimary = baseWhite,
    onBackground = baseBlack
)

val lightStatusColors = MedAiStatusColors(
    success = success,
    warning = warning,
    error = error,
    successContainer = success100,
    warningContainer = warning100,
    errorContainer = error100,
    starRating = gold,
    waiting = warningAlt,
    completed = successAlt,
    cancelled = errorAlt
)

val lightColors = MedAiColors(
    primary = primary600, // Slightly darker blue for better contrast on white
    secondary = secondary,
    accent = accent,
    background = neutral100, // Very light grey, easier on eyes than pure white
    surface = baseWhite,
    onPrimary = baseWhite,
    onSecondary = baseWhite,
    onBackground = baseBlack,
    onSurface = baseBlack,
    neutral = neutral500,
    status = lightStatusColors,
    text = lightTextColors
)
