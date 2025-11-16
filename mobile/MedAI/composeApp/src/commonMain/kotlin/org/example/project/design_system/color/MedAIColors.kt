package org.example.project.design_system.color

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.example.project.design_system.color.lightColors

/**
 * A class that defines the semantic color system for the MedAi app.
 * This class provides more specific, named colors than the default
 * MaterialTheme ColorScheme.
 */
data class MedAiColors(
    // Core
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,

    // Neutrals
    val neutral: Color,

    // Semantic categories
    val status: MedAiStatusColors,
    val text: MedAiTextColors
)

/**
 * Semantic colors for different text roles.
 */
data class MedAiTextColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val onPrimary: Color,
    val onBackground: Color
)

/**
 * Semantic colors for different UI states (success, warning, error).
 */
data class MedAiStatusColors(
    val success: Color,
    val warning: Color,
    val error: Color,
    val successContainer: Color,
    val warningContainer: Color,
    val errorContainer: Color
)

/**
 * The CompositionLocal to access the MedAiColors.
 * This is the "provider" that allows composables to get the current theme colors.
 * It defaults to the light theme.
 */
val LocalMedAiColors = staticCompositionLocalOf { lightColors }
