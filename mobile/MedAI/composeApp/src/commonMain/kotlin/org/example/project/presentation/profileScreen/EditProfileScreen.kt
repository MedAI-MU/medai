package org.example.project.presentation.profileScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.core.presentation.image.rememberImagePicker
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.button.radioButton.MedAIRadioButton
import org.example.project.design_system.component.dayPicker.MedAIDatePickerDialog
import org.example.project.design_system.component.image.MedAIAsyncImage
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.component.textFields.MedAiTextArea
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.auth.UserRole

class EditProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<EditProfileViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val dimensions = LocalDimensions.current
        val scrollState = rememberScrollState()

        var showDatePicker by remember { mutableStateOf(false) }

        // Setup image picker
        val imagePicker = rememberImagePicker { bytes ->
            val fileName = "avatar_${state.user?.id ?: "user"}.jpg"
            viewModel.onEvent(EditProfileEvent.AvatarSelected(bytes, fileName))
        }

        // Initialize form fields by loading profile data on first load
        LaunchedEffect(Unit) {
            viewModel.onEvent(EditProfileEvent.LoadProfile)
        }

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    EditProfileEffect.NavigateBack -> navigator.pop()
                    is EditProfileEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                    is EditProfileEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        MedAIScaffold(
            title = "Edit Profile",
            onBackClick = { viewModel.onEvent(EditProfileEvent.CancelClicked) },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        ) {
            if (state.isLoading && state.user == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MedAITheme.colors.primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = dimensions.extraLarge)
                        .padding(bottom = dimensions.extraExtraLarge),
                    verticalArrangement = Arrangement.spacedBy(dimensions.large),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(dimensions.medium))

                    // 1. Avatar Picker Section
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .clickable { imagePicker.launch() },
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        MedAIAsyncImage(
                            imageUrl = state.user?.avatarUrl,
                            nameForInitials = state.name,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )

                        if (state.isUploadingAvatar) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MedAITheme.colors.primary, modifier = Modifier.size(24.dp))
                            }
                        }

                        // Edit Camera Icon Overlay
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MedAITheme.colors.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change avatar",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(dimensions.medium))

                    // 2. Input Fields
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(dimensions.large)
                    ) {
                        // Name Input
                        Column(verticalArrangement = Arrangement.spacedBy(dimensions.extraSmall)) {
                            MedAIText(text = "Full Name", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                            MedAiTextField(
                                value = state.name,
                                onValueChange = { viewModel.onEvent(EditProfileEvent.NameChanged(it)) },
                                placeholder = "Full Name"
                            )
                        }

                        // Phone Input
                        Column(verticalArrangement = Arrangement.spacedBy(dimensions.extraSmall)) {
                            MedAIText(text = "Phone Number", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                            MedAiTextField(
                                value = state.phone,
                                onValueChange = { viewModel.onEvent(EditProfileEvent.PhoneChanged(it)) },
                                placeholder = "Phone Number"
                            )
                        }

                        // BirthDate Input (Opens Picker)
                        Column(verticalArrangement = Arrangement.spacedBy(dimensions.extraSmall)) {
                            MedAIText(text = "Birth Date", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                            Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
                                MedAiTextField(
                                    value = state.birthDate,
                                    onValueChange = {},
                                    placeholder = "YYYY-MM-DD",
                                    readOnly = true,
                                    enabled = false
                                )
                            }
                        }

                        // Gender Selection
                        Column(verticalArrangement = Arrangement.spacedBy(dimensions.extraSmall)) {
                            MedAIText(text = "Gender", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                            Row(horizontalArrangement = Arrangement.spacedBy(dimensions.large)) {
                                MedAIRadioButton(
                                    selected = state.gender.lowercase() == "male",
                                    onClick = { viewModel.onEvent(EditProfileEvent.GenderChanged("male")) },
                                    label = "Male"
                                )
                                MedAIRadioButton(
                                    selected = state.gender.lowercase() == "female",
                                    onClick = { viewModel.onEvent(EditProfileEvent.GenderChanged("female")) },
                                    label = "Female"
                                )
                            }
                        }

                        // Biography Input (Doctor Only)
                        if (state.user?.role == UserRole.DOCTOR) {
                            Column(verticalArrangement = Arrangement.spacedBy(dimensions.extraSmall)) {
                                MedAIText(text = "Biography", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                                MedAiTextArea(
                                    value = state.bio,
                                    onValueChange = { viewModel.onEvent(EditProfileEvent.BioChanged(it)) },
                                    placeholder = "Biography"
                                )
                            }

                            // About details read-only card
                            Column(verticalArrangement = Arrangement.spacedBy(dimensions.extraSmall)) {
                                MedAIText(text = "About (Read-Only)", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                                MedAiTextArea(
                                    value = state.about,
                                    onValueChange = {},
                                    placeholder = "Detailed doctor biography...",
                                    minLines = 4
                                )
                                MedAIText(
                                    text = "* Detailed professional details editing will be supported in a future backend release.",
                                    style = MedAITheme.textStyle.body.small,
                                    color = MedAITheme.colors.text.secondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(dimensions.medium))

                    // 3. Save & Cancel Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensions.medium)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            MedAIButton(
                                text = "Cancel",
                                onClick = { viewModel.onEvent(EditProfileEvent.CancelClicked) },
                                variant = ButtonVariant.Secondary
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            if (state.isSaving) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = MedAITheme.colors.primary)
                                }
                            } else {
                                MedAIButton(
                                    text = "Save",
                                    onClick = { viewModel.onEvent(EditProfileEvent.SaveClicked) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Date Picker Sheet
        if (showDatePicker) {
            MedAIDatePickerDialog(
                onDateSelected = { formattedDate ->
                    viewModel.onEvent(EditProfileEvent.BirthDateChanged(formattedDate))
                },
                onDismiss = { showDatePicker = false },
                allowFutureDates = false,
                outputFormat = "YYYY-MM-DD"
            )
        }
    }
}
