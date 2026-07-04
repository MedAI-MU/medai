package org.example.project.presentation.loginScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.flow.collectLatest
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.email_or_mobile_label
import medai.composeapp.generated.resources.email_placeholder
import medai.composeapp.generated.resources.forgot_password
import medai.composeapp.generated.resources.log_in
import medai.composeapp.generated.resources.login_subtitle
import medai.composeapp.generated.resources.password_label
import medai.composeapp.generated.resources.password_placeholder
import medai.composeapp.generated.resources.welcome_back
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.component.textFields.MedAiPasswordTextField
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.MainContainerScreen
import org.example.project.presentation.pendingApprovalScreen.PendingApprovalScreen
import org.example.project.presentation.loginScreen.component.InputLabel
import org.example.project.presentation.loginScreen.component.SignUpLink
import org.example.project.presentation.loginScreen.component.SocialLoginSection
import org.example.project.presentation.signUpScreen.SignUpScreen
import org.jetbrains.compose.resources.stringResource

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<LoginViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        // Handle Side Effects (Navigation & Errors)
        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is LoginEffect.NavigateToHome -> navigator.replaceAll(MainContainerScreen())
                    is LoginEffect.NavigateToPendingApproval -> navigator.replaceAll(PendingApprovalScreen())
                    is LoginEffect.NavigateToSignUp -> navigator.push(SignUpScreen())
                    is LoginEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        MedAIScaffold(
            title = stringResource(Res.string.log_in),
            onBackClick = { navigator.pop() },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MedAITheme.dimensions.extraLarge),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                // Header
                MedAIText(
                    text = stringResource(Res.string.welcome_back),
                    style = MedAITheme.textStyle.headline.large.copy(
                        color = MedAITheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(MedAITheme.dimensions.small))
                MedAIText(
                    text = stringResource(Res.string.login_subtitle),
                    style = MedAITheme.textStyle.body.medium,
                    color = MedAITheme.colors.text.secondary
                )

                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraExtraLarge))

                // Email Input
                InputLabel(text = stringResource(Res.string.email_or_mobile_label))
                MedAiTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
                    placeholder = stringResource(Res.string.email_placeholder)
                )

                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                // Password Input
                InputLabel(text = stringResource(Res.string.password_label))
                MedAiPasswordTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                    placeholder = stringResource(Res.string.password_placeholder)
                )

                // Forgot Password
                Spacer(modifier = Modifier.height(MedAITheme.dimensions.small))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    MedAIText(
                        text = stringResource(Res.string.forgot_password),
                        style = MedAITheme.textStyle.label.medium,
                        color = MedAITheme.colors.primary,
                        modifier = Modifier.clickable { /* TODO */ }
                    )
                }

                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraExtraLarge))

                // Login Button
                if (state.isLoading) {
                    // Loading Indicator
                    CircularProgressIndicator(
                        color = MedAITheme.colors.primary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    MedAIButton(
                        text = stringResource(Res.string.log_in),
                        onClick = { viewModel.onEvent(LoginEvent.LoginClicked) },
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                // Social Login
                SocialLoginSection()

                Spacer(modifier = Modifier.weight(1f))

                // Sign Up Link
                SignUpLink(
                    onSignUpClick = { navigator.push(SignUpScreen()) }
                )
                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))
            }
        }
    }
}
