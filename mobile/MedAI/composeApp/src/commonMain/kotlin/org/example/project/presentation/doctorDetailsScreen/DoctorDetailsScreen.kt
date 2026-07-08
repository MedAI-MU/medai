package org.example.project.presentation.doctorDetailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.about_doctor
import medai.composeapp.generated.resources.book_appointment_button
import medai.composeapp.generated.resources.doctor_details_title
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.bookingScreen.BookingScreen
import org.example.project.presentation.chatScreen.ChatScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class DoctorDetailsScreen(val doctorId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<DoctorDetailsViewModel> { parametersOf(doctorId) }
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val dimensions = LocalDimensions.current

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when(effect) {
                    DoctorDetailsEffect.NavigateBack -> navigator.pop()
                    DoctorDetailsEffect.NavigateToBooking -> navigator.push(BookingScreen(doctorId))
                    is DoctorDetailsEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                    is DoctorDetailsEffect.NavigateToChat -> navigator.push(
                        ChatScreen(
                            effect.doctorId,
                            effect.doctorName
                        )
                    )
                }
            }
        }

        MedAIScaffold(
            title = stringResource(Res.string.doctor_details_title),
            onBackClick = { viewModel.onEvent(DoctorDetailsEvent.BackClicked) },
            actions = {
                IconButton(onClick = {}) { Icon(Icons.AutoMirrored.Filled.HelpOutline, null, tint = MedAITheme.colors.onPrimary) }
                IconButton(onClick = {}) { Icon(Icons.Default.FavoriteBorder, null, tint = MedAITheme.colors.onPrimary) }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MedAITheme.colors.primary
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // --- Header Profile Section ---
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(dimensions.spacing64 * 3), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.onPrimary)
                    }
                } else {
                    state.doctor?.let { doctor ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensions.extraLarge),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Image
                            Box(
                                modifier = Modifier
                                    .size(dimensions.spacing64 + dimensions.extraExtraLarge)
                                    .clip(CircleShape)
                                    .background(MedAITheme.colors.neutral)
                            ) {
                                // AsyncImage(doctor.imageUrl)
                            }
                            Spacer(modifier = Modifier.height(dimensions.large))

                            MedAIText(
                                text = doctor.name,
                                style = MedAITheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold),
                                color = MedAITheme.colors.onPrimary
                            )
                            MedAIText(
                                text = doctor.specialty,
                                style = MedAITheme.textStyle.body.medium,
                                color = MedAITheme.colors.onPrimary.copy(alpha = 0.9f)
                            )

                            Spacer(modifier = Modifier.height(dimensions.large))

                            // Stats Row (Rating, Reviews)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(dimensions.radiusRound))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = dimensions.large, vertical = dimensions.small),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimensions.large)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(dimensions.large))
                                    Spacer(modifier = Modifier.width(dimensions.extraSmall))
                                    MedAIText(text = doctor.rating.toString(), color = Color.White, style = MedAITheme.textStyle.label.medium)
                                }
                                MedAIText(text = "|", color = Color.White.copy(alpha = 0.5f), style = MedAITheme.textStyle.label.medium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ChatBubble, null, tint = Color.White, modifier = Modifier.size(dimensions.large))
                                    Spacer(modifier = Modifier.width(dimensions.extraSmall))
                                    MedAIText(text = "${doctor.reviewCount} Reviews", color = Color.White, style = MedAITheme.textStyle.label.medium)
                                }
                            }
                        }
                    }
                }

                // --- White Body Content ---
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = dimensions.extraExtraLarge, topEnd = dimensions.extraExtraLarge))
                        .background(MedAITheme.colors.background)
                        .padding(dimensions.extraLarge)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Action Buttons (Schedule, Call, Video, Chat)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ActionButton(Icons.Default.Call, isSelected = false, onClick = {})
                            ActionButton(Icons.Default.Videocam, isSelected = false, onClick = {})
                            ActionButton(
                                icon = Icons.Default.ChatBubble,
                                isSelected = false,
                                onClick = { viewModel.onEvent(DoctorDetailsEvent.MessageClicked) }
                            )
                        }

                        Spacer(modifier = Modifier.height(dimensions.spacing64 / 2))

                        // About / Bio
                        MedAIText(text = stringResource(Res.string.about_doctor), style = MedAITheme.textStyle.title.large.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(dimensions.small))
                        state.doctor?.let {
                            MedAIText(
                                text = it.bio,
                                style = MedAITheme.textStyle.body.medium,
                                color = MedAITheme.colors.text.secondary,
                                textAlign = TextAlign.Justify
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.height(dimensions.extraLarge))

                        // Book Button
                        if (state.doctor != null) {
                            MedAIButton(
                                text = stringResource(Res.string.book_appointment_button),
                                onClick = { viewModel.onEvent(DoctorDetailsEvent.BookClicked) },
                                variant = ButtonVariant.Primary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
        val dimensions = LocalDimensions.current
        Box(
            modifier = Modifier
                .size(dimensions.spacing64 - dimensions.small) // approx 56dp
                .clip(CircleShape)
                .background(if (isSelected) MedAITheme.colors.primary else MedAITheme.colors.primary.copy(alpha = 0.1f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MedAITheme.colors.onPrimary else MedAITheme.colors.primary
            )
        }
    }
}
