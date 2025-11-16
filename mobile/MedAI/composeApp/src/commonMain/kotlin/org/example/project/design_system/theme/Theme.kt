package org.example.project.design_system.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import org.example.project.design_system.color.LocalMedAiColors
import org.example.project.design_system.color.MedAiColors
import org.example.project.design_system.text_style.LocalMedAiTextStyle
import org.example.project.design_system.text_style.MedAiTextStyle

object Theme{
    val color: MedAiColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMedAiColors.current

    val textStyle: MedAiTextStyle
        @Composable
        @ReadOnlyComposable
        get() = LocalMedAiTextStyle.current
}
