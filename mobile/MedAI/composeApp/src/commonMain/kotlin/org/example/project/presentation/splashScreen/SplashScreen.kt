package org.example.project.presentation.splashScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import org.example.project.core.data.OnboardingStorage
import org.example.project.design_system.component.text.MedAIText
import org.example.project.presentation.onboardingScreen.OnboardingScreen
import org.example.project.presentation.welcomeScreen.WelcomeScreen

class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val storage = remember { OnboardingStorage() }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            MedAIText(text = "Splash Screen (Logo)...",
                style = TextStyle())
        }


        LaunchedEffect(Unit) {
            delay(2000)
            if (storage.isOnboardingCompleted()) {
                navigator.replace(WelcomeScreen())
            } else {
                navigator.replace(OnboardingScreen())
            }
        }
    }
}
