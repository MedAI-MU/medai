package org.example.project.presentation.splashScreen

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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.core.data.OnboardingStorage
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.onboardingScreen.OnboardingScreen
import org.example.project.presentation.welcomeScreen.WelcomeScreen
import org.koin.compose.koinInject
import org.example.project.domain.repository.UserSessionManager
import org.example.project.domain.model.UserRole
import kotlinx.coroutines.flow.first
import org.example.project.presentation.doctor.dashboard.DoctorDashboardScreen
import org.example.project.presentation.secretary.dashboard.SecretaryDashboardScreen


class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val storage = remember { OnboardingStorage() }

        val scale = remember { Animatable(0.8f) }
        val alpha = remember { Animatable(0f) }
        val logoOffset = remember { Animatable(50f) }

        val userSessionManager = koinInject<UserSessionManager>()

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

            if (userSessionManager.isUserLoggedIn.first()) {
                val role = userSessionManager.getUserRole()
                when (role) {
                    UserRole.DOCTOR -> navigator.replace(DoctorDashboardScreen())
                    UserRole.SECRETARY -> navigator.replace(SecretaryDashboardScreen())
                    else -> navigator.replace(WelcomeScreen()) // Default to patient flow (Welcome -> Home)
                }
            } else {
                if (storage.isOnboardingCompleted()) {
                    navigator.replace(WelcomeScreen())
                } else {
                    navigator.replace(OnboardingScreen())
                }
            }
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
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "MedAI Logo",
            modifier = Modifier.size(iconSize),
            tint = tint
        )

        Spacer(modifier = Modifier.height(16.dp))

        MedAIText(
            text = "MedAI",
            style = MedAITheme.textStyle.headline.large.copy(
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )
    }
}
