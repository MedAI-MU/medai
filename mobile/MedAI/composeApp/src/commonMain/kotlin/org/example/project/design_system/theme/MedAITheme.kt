package org.example.project.design_system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.example.project.design_system.color.LocalMedAiColors
import org.example.project.design_system.color.darkColors
import org.example.project.design_system.color.lightColors
import org.example.project.design_system.text_style.LocalMedAiTextStyle
import org.example.project.design_system.text_style.defaultMedAITextStyle

@Composable
fun MedAITheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
){
    val theme = if (isDarkTheme) darkColors else lightColors
    CompositionLocalProvider(
        LocalMedAiColors provides theme,
        LocalMedAiTextStyle provides defaultMedAITextStyle
    ) {
        content()
    }
}
