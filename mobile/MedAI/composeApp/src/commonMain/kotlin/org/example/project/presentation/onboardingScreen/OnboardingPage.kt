package org.example.project.presentation.onboardingScreen

import org.example.project.core.presentation.util.UiText
import org.jetbrains.compose.resources.DrawableResource

data class OnboardingPage(
    val title: UiText,
    val description: UiText,
    val image: DrawableResource
)
