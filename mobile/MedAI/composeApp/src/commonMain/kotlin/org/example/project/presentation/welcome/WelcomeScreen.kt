package org.example.project.presentation.welcome


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    MedAIScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MedAITheme.colors.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(600)) + fadeIn()
            ) {
                MedAILogo(
                    tint = Color(0xFF00E5FF),
                    textColor = Color(0xFF00E5FF)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(800)) + fadeIn()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    MedAIText(
                        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        style = MedAITheme.textStyle.body.medium,
                        color = MedAITheme.colors.text.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    MedAIButton(
                        text = "Log In",
                        onClick = onLoginClick,
                        variant = ButtonVariant.Primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MedAIButton(
                        text = "Sign Up",
                        onClick = onSignUpClick,
                        variant = ButtonVariant.Secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
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