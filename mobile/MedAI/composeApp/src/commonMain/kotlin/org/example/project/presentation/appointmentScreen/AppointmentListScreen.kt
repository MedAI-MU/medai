package org.example.project.presentation.appointmentScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import org.example.project.core.presentation.util.toUiString
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.presentation.appointmentScreen.component.MedAISegmentedControl
import org.example.project.presentation.appointmentScreen.sheet.ReviewSheet

class AppointmentListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<AppointmentViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        var showReviewSheet by remember { mutableStateOf(false) }
        var selectedReviewId by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            viewModel.onEvent(AppointmentEvent.Refresh)
        }
        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is AppointmentEffect.CloseSheet -> {
                        showReviewSheet = false
                    }

                    is AppointmentEffect.ShowToast -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }

                    else -> {}
                }
            }
        }

        if (showReviewSheet && selectedReviewId != null) {
            ReviewSheet(
                onDismiss = { showReviewSheet = false },
                onSubmit = { rating, comment ->
                    viewModel.onEvent(
                        AppointmentEvent.OnSubmitReview(
                            selectedReviewId!!,
                            rating,
                            comment
                        )
                    )
                }
            )
        }


        MedAIScaffold(
            title = "My Appointments",
            onBackClick = { navigator.pop() },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MedAITheme.colors.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // 1. Segmented Control (Pill Tabs)
                Box(modifier = Modifier.padding(horizontal = MedAITheme.dimensions.extraLarge, vertical = MedAITheme.dimensions.large)) {
                    MedAISegmentedControl(
                        items = listOf("Upcoming", "Finished", "Cancelled"),
                        selectedIndex = when (state.selectedTab) {
                            AppointmentDetailStatus.UPCOMING -> 0
                            AppointmentDetailStatus.FINISHED -> 1
                            AppointmentDetailStatus.CANCELLED -> 2
                        },
                        onIndexChanged = { index ->
                            val status = when (index) {
                                0 -> AppointmentDetailStatus.UPCOMING
                                1 -> AppointmentDetailStatus.FINISHED
                                else -> AppointmentDetailStatus.CANCELLED
                            }
                            viewModel.onEvent(AppointmentEvent.OnTabSelected(status))
                        }
                    )
                }

                // 2. List Content
                if (state.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                } else if (state.appointments.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        MedAIText(
                            "No appointments found", color = MedAITheme.colors.text.secondary,
                            style = MedAITheme.textStyle.body.medium
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = MedAITheme.dimensions.extraLarge, end = MedAITheme.dimensions.extraLarge, bottom = MedAITheme.dimensions.extraLarge),
                        verticalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.large)
                    ) {
                        items(state.appointments) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                onClick = { navigator.push(AppointmentDetailScreen(appointment.id)) },
                                onReviewClick = {
                                    selectedReviewId = appointment.id
                                    showReviewSheet = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: AppointmentDetail,
    onClick: () -> Unit,
    onReviewClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(MedAITheme.dimensions.elevationMedium, RoundedCornerShape(MedAITheme.dimensions.radiusExtraLarge))
            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusExtraLarge))
            .background(MedAITheme.colors.surface)
            .clickable { onClick() }
            .padding(MedAITheme.dimensions.large)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    MedAIText(text = appointment.doctorName, style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold))
                    MedAIText(text = appointment.specialty, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(MedAITheme.dimensions.radiusMedium))
                        .background(MedAITheme.colors.primary.copy(alpha = 0.1f))
                        .padding(horizontal = MedAITheme.dimensions.small, vertical = MedAITheme.dimensions.extraSmall)
                ) {
                    MedAIText("★ ${appointment.doctorRating}", style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.primary)
                }
            }

            Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))
            HorizontalDivider(color = MedAITheme.colors.neutral.copy(0.3f))
            Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

            Row(verticalAlignment = Alignment.CenterVertically) {
                MedAIText(text = "📅", style = MedAITheme.textStyle.body.medium)
                Spacer(modifier = Modifier.width(MedAITheme.dimensions.small))
                MedAIText(
                    text = appointment.date.toUiString(),
                    style = MedAITheme.textStyle.label.medium,
                    color = MedAITheme.colors.text.primary
                )
            }

            Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.small)) {
                if (appointment.canRebook) {
                    OutlinedButton(
                        onClick = { /* Rebook Logic */ },
                        modifier = Modifier.weight(1f).height(MedAITheme.dimensions.spacing48),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MedAITheme.colors.primary)
                    ) {
                        Text("Re-book")
                    }
                }

                if (appointment.canAddReview) {
                    Button(
                        onClick = onReviewClick,
                        modifier = Modifier.weight(1f).height(MedAITheme.dimensions.spacing48),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
                    ) {
                        Text("Add Review")
                    }
                } else if (appointment.status == AppointmentDetailStatus.UPCOMING) {
                    Button(
                        onClick = { onClick() },
                        modifier = Modifier.weight(1f).height(MedAITheme.dimensions.spacing48),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
                    ) {
                        Text("Details")
                    }
                }
            }
        }
    }
}
