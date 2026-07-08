package org.example.project.design_system.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 24.dp,
    val extraExtraLarge: Dp = 32.dp,
    val spacing48: Dp = 48.dp,
    val spacing64: Dp = 64.dp,

    val radiusSmall: Dp = 4.dp,
    val radiusMedium: Dp = 8.dp,
    val radiusLarge: Dp = 12.dp,
    val radiusExtraLarge: Dp = 16.dp,
    val radiusRound: Dp = 50.dp,

    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val iconExtraLarge: Dp = 48.dp,

    val elevationSmall: Dp = 2.dp,
    val elevationMedium: Dp = 4.dp,
    val elevationLarge: Dp = 8.dp,
)

val LocalDimensions = compositionLocalOf { Dimensions() }
