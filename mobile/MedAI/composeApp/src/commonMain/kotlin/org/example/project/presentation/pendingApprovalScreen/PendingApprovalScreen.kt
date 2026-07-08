package org.example.project.presentation.pendingApprovalScreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.check_status
import medai.composeapp.generated.resources.logout
import medai.composeapp.generated.resources.pending_approval_desc
import medai.composeapp.generated.resources.pending_approval_password_label
import medai.composeapp.generated.resources.pending_approval_reassure
import medai.composeapp.generated.resources.pending_approval_status
import medai.composeapp.generated.resources.pending_approval_subtitle
import medai.composeapp.generated.resources.pending_approval_title
import medai.composeapp.generated.resources.still_pending_message
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.component.textFields.MedAiPasswordTextField
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.MainContainerScreen
import org.example.project.presentation.loginScreen.component.InputLabel
import org.example.project.presentation.welcomeScreen.WelcomeScreen
import org.jetbrains.compose.resources.stringResource

class PendingApprovalScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<PendingApprovalViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        val stillPendingMsg = stringResource(Res.string.still_pending_message)

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is PendingApprovalEffect.NavigateToHome -> {
                        navigator.replaceAll(MainContainerScreen())
                    }
                    is PendingApprovalEffect.NavigateToWelcome -> {
                        navigator.replaceAll(WelcomeScreen())
                    }
                    is PendingApprovalEffect.ShowStillPending -> {
                        snackbarHostState.showSnackbar(stillPendingMsg)
                    }
                    is PendingApprovalEffect.ShowError -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

        MedAIScaffold(
            title = stringResource(Res.string.pending_approval_subtitle),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MedAITheme.colors.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MedAITheme.dimensions.extraLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                // --- Under Review Illustration / Animation ---
                UnderReviewAnimation()

                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                // --- Content Card ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(MedAITheme.dimensions.radiusLarge))
                        .background(MedAITheme.colors.surface)
                        .border(
                            width = 1.dp,
                            color = MedAITheme.colors.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(MedAITheme.dimensions.radiusLarge)
                        )
                        .padding(MedAITheme.dimensions.extraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusSmall))
                            .background(MedAITheme.colors.status.waiting.copy(alpha = 0.15f))
                            .padding(
                                horizontal = MedAITheme.dimensions.medium,
                                vertical = MedAITheme.dimensions.extraSmall
                            )
                    ) {
                        MedAIText(
                            text = stringResource(Res.string.pending_approval_status),
                            style = MedAITheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                            color = MedAITheme.colors.status.waiting
                        )
                    }

                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

                    // Title
                    MedAIText(
                        text = stringResource(Res.string.pending_approval_title),
                        style = MedAITheme.textStyle.headline.medium.copy(fontWeight = FontWeight.Bold),
                        color = MedAITheme.colors.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

                    // Description
                    MedAIText(
                        text = stringResource(Res.string.pending_approval_desc),
                        style = MedAITheme.textStyle.body.medium,
                        color = MedAITheme.colors.text.secondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

                    // Reassurance Text
                    MedAIText(
                        text = stringResource(Res.string.pending_approval_reassure),
                        style = MedAITheme.textStyle.label.medium.copy(fontWeight = FontWeight.Medium),
                        color = MedAITheme.colors.text.tertiary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraExtraLarge))

                // --- Password Input Form ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    InputLabel(text = stringResource(Res.string.pending_approval_password_label))
                    MedAiPasswordTextField(
                        value = state.password,
                        onValueChange = { viewModel.onEvent(PendingApprovalEvent.PasswordChanged(it)) },
                        placeholder = "••••••••"
                    )
                }

                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraExtraLarge))

                // --- Action Buttons ---
                if (state.isCheckingStatus) {
                    CircularProgressIndicator(
                        color = MedAITheme.colors.primary,
                        modifier = Modifier.padding(vertical = MedAITheme.dimensions.medium)
                    )
                } else {
                    MedAIButton(
                        text = stringResource(Res.string.check_status),
                        onClick = { viewModel.onEvent(PendingApprovalEvent.CheckStatusClicked) },
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

                    MedAIButton(
                        text = stringResource(Res.string.logout),
                        onClick = { viewModel.onEvent(PendingApprovalEvent.LogoutClicked) },
                        variant = ButtonVariant.Secondary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))
            }
        }
    }
}

@Composable
private fun UnderReviewAnimation() {
    val infiniteTransition = rememberInfiniteTransition()

    // Pulse animations for background circles
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(160.dp)
    ) {
        // Outer pulsing ring 2
        Box(
            modifier = Modifier
                .scale(scale2)
                .alpha(alpha2)
                .size(100.dp)
                .clip(CircleShape)
                .background(MedAITheme.colors.primary.copy(alpha = 0.3f))
        )

        // Outer pulsing ring 1
        Box(
            modifier = Modifier
                .scale(scale1)
                .alpha(alpha1)
                .size(100.dp)
                .clip(CircleShape)
                .background(MedAITheme.colors.accent.copy(alpha = 0.4f))
        )

        // Core icon container
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MedAITheme.colors.surface,
                            MedAITheme.colors.background
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    color = MedAITheme.colors.primary,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.HourglassEmpty,
                contentDescription = "Under Review",
                tint = MedAITheme.colors.primary,
                modifier = Modifier.size(MedAITheme.dimensions.iconExtraLarge)
            )
        }
    }
}
