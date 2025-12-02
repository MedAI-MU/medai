package org.example.project.presentation.setPasswordScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.confirm_password_label
import medai.composeapp.generated.resources.create_password_btn
import medai.composeapp.generated.resources.password_label
import medai.composeapp.generated.resources.password_placeholder
import medai.composeapp.generated.resources.set_password_desc
import medai.composeapp.generated.resources.set_password_title
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.component.textFields.MedAiPasswordTextField
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.loginScreen.component.InputLabel
import org.jetbrains.compose.resources.stringResource

class SetPasswordScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SetPasswordViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        // --- Handle Side Effects ---
        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is SetPasswordEffect.NavigateToLogin -> {
                        // Clear stack and go to Login (Simulated)
                        navigator.popUntilRoot()
                    }
                    is SetPasswordEffect.ShowError -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

        // --- UI ---
        MedAIScaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                MedAiAppBar(
                    title = stringResource(Res.string.set_password_title),
                    onBackClick = { navigator.pop() }
                )
            },
            containerColor = MedAITheme.colors.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Description Text
                MedAIText(
                    text = stringResource(Res.string.set_password_desc),
                    style = MedAITheme.textStyle.body.medium,
                    color = MedAITheme.colors.text.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Input 1: Password
                InputLabel(stringResource(Res.string.password_label))
                MedAiPasswordTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(SetPasswordEvent.PasswordChanged(it)) },
                    placeholder = stringResource(Res.string.password_placeholder)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Input 2: Confirm Password
                InputLabel(stringResource(Res.string.confirm_password_label))
                MedAiPasswordTextField(
                    value = state.confirmPassword,
                    onValueChange = { viewModel.onEvent(SetPasswordEvent.ConfirmPasswordChanged(it)) },
                    placeholder = stringResource(Res.string.password_placeholder)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Submit Button
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MedAITheme.colors.primary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    MedAIButton(
                        text = stringResource(Res.string.create_password_btn),
                        onClick = { viewModel.onEvent(SetPasswordEvent.SubmitClicked) },
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
