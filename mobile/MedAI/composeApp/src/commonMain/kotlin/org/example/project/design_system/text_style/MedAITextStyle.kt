package org.example.project.design_system.text_style

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

data class MedAiTextStyle(
    val headline: SizedTextStyle,
    val title: SizedTextStyle,
    val body: SizedTextStyle,
    val label: SizedTextStyle
)

data class SizedTextStyle(
    val large: TextStyle,
    val medium: TextStyle,
    val small: TextStyle,
)

/**
 * The CompositionLocal to access the MedAiTextStyle.
 * This is the "provider" that allows composables to get the current theme typography.
 * It defaults to the 'defaultMedAiTextStyle' which we define in Type.kt.
 */
val LocalMedAiTextStyle = staticCompositionLocalOf { defaultMedAITextStyle }
