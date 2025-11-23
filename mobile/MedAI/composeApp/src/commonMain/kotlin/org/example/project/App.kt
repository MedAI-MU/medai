package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.compose_multiplatform
import org.example.project.design_system.component.bottomNavigation.MedAIBottomNavigation
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.button.MedAICardButton
import org.example.project.design_system.component.button.MedAICircularButton
import org.example.project.design_system.component.button.radioButton.MedAIRadioButton
import org.example.project.design_system.component.dayPicker.MedAIDateCard
import org.example.project.design_system.component.dropDown.MedAIDropdown
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.switches.MedAISwitch
import org.example.project.design_system.component.textFields.MedAiDateTextField
import org.example.project.design_system.component.textFields.MedAiPasswordTextField
import org.example.project.design_system.component.textFields.MedAiTextArea
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.home.HomeScreen
import org.example.project.presentation.splash.SplashScreen
import org.example.project.presentation.welcome.WelcomeScreen

// Simple Navigation State
enum class AppScreen {
    Splash,
    Welcome,
    Home
}

@Composable
@Preview
fun App() {
    MedAITheme {
        var currentScreen by remember { mutableStateOf(AppScreen.Splash) }

        Crossfade(
            targetState = currentScreen,
            animationSpec = tween(durationMillis = 600)
        ) { screen ->
            when (screen) {
                AppScreen.Splash -> {
                    SplashScreen(
                        onSplashFinished = {
                            currentScreen = AppScreen.Welcome
                        }
                    )
                }
                AppScreen.Welcome -> {
                    WelcomeScreen(
                        onLoginClick = {
                            currentScreen = AppScreen.Home
                        },
                        onSignUpClick = {
                            // Handle Sign Up navigation
                        }
                    )
                }
                AppScreen.Home -> {
                    HomeScreen()
                }
            }
        }
    }
}