package org.example.project

import androidx.compose.animation.AnimatedVisibility

import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.splashScreen.SplashScreen

@Composable
@Preview
fun App() {
    MedAITheme {
        // Initialize the app starting with SplashScreen
        Navigator(screen = SplashScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
