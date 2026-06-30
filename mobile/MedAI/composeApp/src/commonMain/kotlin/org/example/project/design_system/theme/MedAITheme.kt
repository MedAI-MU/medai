package org.example.project.design_system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
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
    val dimensions = Dimensions()

    val materialColorScheme = if (isDarkTheme) {
        darkColorScheme(
            primary = theme.primary,
            secondary = theme.secondary,
            background = theme.background,
            surface = theme.surface,
            onPrimary = theme.onPrimary,
            onSecondary = theme.onSecondary,
            onBackground = theme.onBackground,
            onSurface = theme.onSurface,
            error = theme.status.error
        )
    } else {
        lightColorScheme(
            primary = theme.primary,
            secondary = theme.secondary,
            background = theme.background,
            surface = theme.surface,
            onPrimary = theme.onPrimary,
            onSecondary = theme.onSecondary,
            onBackground = theme.onBackground,
            onSurface = theme.onSurface,
            error = theme.status.error
        )
    }

    MaterialTheme(
        colorScheme = materialColorScheme
    ) {
        CompositionLocalProvider(
            LocalMedAiColors provides theme,
            LocalMedAiTextStyle provides defaultMedAITextStyle,
            LocalDimensions provides dimensions
        ) {
            content()
        }
    }
}
