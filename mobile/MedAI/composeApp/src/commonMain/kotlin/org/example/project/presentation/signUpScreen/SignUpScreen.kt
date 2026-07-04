package org.example.project.presentation.signUpScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.flow.collectLatest
import cafe.adriel.voyager.koin.getScreenModel
import medai.composeapp.generated.resources.*
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.dayPicker.MedAIDatePickerDialog
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.component.textFields.MedAiPasswordTextField
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.MainContainerScreen
import org.example.project.presentation.pendingApprovalScreen.PendingApprovalScreen
import org.example.project.domain.model.auth.UserRole
import org.example.project.presentation.loginScreen.component.InputLabel
import org.example.project.presentation.loginScreen.component.SocialLoginSection
import org.example.project.presentation.signUpScreen.component.SignUpTopBar
import org.jetbrains.compose.resources.stringResource

class SignUpScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SignUpViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        if (state.showDatePicker) {
            MedAIDatePickerDialog(
                onDateSelected = { date ->
                    viewModel.onEvent(SignUpEvent.DateOfBirthChanged(date))
                },
                onDismiss = {
                    viewModel.onEvent(SignUpEvent.ToggleDatePicker(false))
                }
            )
        }

        // Effect: Handle Success or Error
        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is SignUpEffect.NavigateToHome -> navigator.replaceAll(MainContainerScreen())
                    is SignUpEffect.NavigateToPendingApproval -> navigator.replaceAll(PendingApprovalScreen())
                    is SignUpEffect.NavigateToLogin -> navigator.pop()
                    is SignUpEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }


        MedAIScaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MedAITheme.colors.background
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                // 1. Custom Top Bar
                SignUpTopBar(onBackClick = { navigator.pop() })

                // 2. Scrollable Form
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = MedAITheme.dimensions.extraLarge),
                    horizontalAlignment = Alignment.Start,
                    contentPadding = PaddingValues(top = MedAITheme.dimensions.extraLarge, bottom = MedAITheme.dimensions.extraLarge)
                ) {
                    item {
                        // --- Full Name ---
                        InputLabel(stringResource(Res.string.full_name_label))
                        MedAiTextField(
                            value = state.fullName,
                            onValueChange = { viewModel.onEvent(SignUpEvent.FullNameChanged(it)) },
                            placeholder = stringResource(Res.string.full_name_placeholder)
                        )
                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

                        // --- Password ---
                        InputLabel(stringResource(Res.string.password_label))
                        MedAiPasswordTextField(
                            value = state.password,
                            onValueChange = { viewModel.onEvent(SignUpEvent.PasswordChanged(it)) },
                            placeholder = stringResource(Res.string.password_placeholder)
                        )
                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

                        // --- Email ---
                        InputLabel(stringResource(Res.string.email_label))
                        MedAiTextField(
                            value = state.email,
                            onValueChange = { viewModel.onEvent(SignUpEvent.EmailChanged(it)) },
                            placeholder = stringResource(Res.string.email_placeholder)
                        )
                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

                        // --- Mobile ---
                        InputLabel(stringResource(Res.string.mobile_number_label))
                        MedAiTextField(
                            value = state.mobile,
                            onValueChange = { viewModel.onEvent(SignUpEvent.MobileChanged(it)) },
                            placeholder = stringResource(Res.string.mobile_number_placeholder)
                        )
                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

                        // --- Date of Birth ---
                        InputLabel(stringResource(Res.string.dob_label))
                        // 2. The Interaction Field
                        Box(modifier = Modifier.fillMaxWidth()) {
                            MedAiTextField(
                                value = state.dob,
                                onValueChange = { }, // Read only, ignored
                                placeholder = stringResource(Res.string.dob_placeholder),
                                readOnly = true,
                                enabled = false, // Disables typing but we wrap in Box for clicks
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Select Date",
                                        tint = MedAITheme.colors.primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // 4. Invisible overlay to catch the click
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable {
                                        viewModel.onEvent(SignUpEvent.ToggleDatePicker(true))
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraSmall))

                        // --- Role Selection ---
                        InputLabel("I am a:")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.small)
                        ) {
                            UserRole.entries.filter { it != UserRole.MANAGER }.forEach { role ->
                                val isSelected = state.selectedRole == role
                                val label = when(role) {
                                    UserRole.PATIENT -> "Patient"
                                    UserRole.DOCTOR -> "Doctor"
                                    UserRole.SECRETARY -> "SECRETARY"
                                    UserRole.MANAGER -> "Manager"
                                }

                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.onEvent(SignUpEvent.RoleChanged(role)) },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MedAITheme.colors.primary,
                                        selectedLabelColor = MedAITheme.colors.text.onPrimary,
                                        containerColor = MedAITheme.colors.surface,
                                        labelColor = MedAITheme.colors.text.primary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                        // --- Terms Text ---
                        MedAIText(
                            text = stringResource(Res.string.terms_agreement),
                            style = MedAITheme.textStyle.label.small,
                            color = MedAITheme.colors.text.secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = MedAITheme.dimensions.large)
                        )
                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                        // --- Sign Up Button ---
                        if (state.isLoading) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MedAITheme.colors.primary)
                            }
                        } else {

                            MedAIButton(
                                text = stringResource(Res.string.sign_up),
                                onClick = { viewModel.onEvent(SignUpEvent.SignUpClicked) },
                                variant = ButtonVariant.Primary
                            )
                        }

                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                        // --- Social Login ---
                        SocialLoginSection()

                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                        // --- Login Link ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            MedAIText(
                                text = stringResource(Res.string.already_have_account),
                                color = MedAITheme.colors.text.secondary,
                                style = MedAITheme.textStyle.label.medium
                            )
                            MedAIText(
                                text = stringResource(Res.string.log_in),
                                color = MedAITheme.colors.primary,
                                style = MedAITheme.textStyle.label.large.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.clickable { navigator.pop() }
                            )
                        }
                    }
                }
            }
        }
    }
}
