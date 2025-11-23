package org.example.project.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }
    val logoOffset = remember { Animatable(50f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
        }
        launch {
            alpha.animateTo(1f, animationSpec = tween(800))
        }
        launch {
            logoOffset.animateTo(0f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
        }
        delay(2000)
        onSplashFinished()
    }

    val brandGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF00E5FF),
            MedAITheme.colors.primary
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brandGradient),
        contentAlignment = Alignment.Center
    ) {
        MedAILogo(
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .offset(y = logoOffset.value.dp),
            tint = MedAITheme.colors.text.onPrimary,
            textColor = MedAITheme.colors.text.onPrimary
        )
    }
}



@Composable
fun MedAILogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 100.dp,
    tint: Color,
    textColor: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TODO: Replace Icons.Default.Favorite with: painterResource(Res.drawable.ic_logo)
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "HealthTrack Logo",
            modifier = Modifier.size(iconSize),
            tint = tint
        )

        Spacer(modifier = Modifier.height(16.dp))

        MedAIText(
            text = "HealthTrack",
            style = MedAITheme.textStyle.headline.large.copy(
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )
    }
}