package org.example.project.presentation.bookingScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.age_label
import medai.composeapp.generated.resources.another_person_label
import medai.composeapp.generated.resources.available_time_title
import medai.composeapp.generated.resources.book_appointment_button
import medai.composeapp.generated.resources.booking_screen_title
import medai.composeapp.generated.resources.describe_problem_label
import medai.composeapp.generated.resources.describe_problem_placeholder
import medai.composeapp.generated.resources.full_name_label
import medai.composeapp.generated.resources.gender_label
import medai.composeapp.generated.resources.month
import medai.composeapp.generated.resources.no_slots_available
import medai.composeapp.generated.resources.patient_details_title
import medai.composeapp.generated.resources.upcoming_schedule_title
import medai.composeapp.generated.resources.yourself_label
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.dayPicker.MedAIDateCard
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.component.textFields.MedAiTextArea
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.bookingScreen.component.PatientTypeChip
import org.example.project.presentation.bookingScreen.component.TimeSlotChip
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class BookingScreen(val doctorId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<BookingViewModel> { parametersOf(doctorId) }
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val dimensions = LocalDimensions.current

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when(effect) {
                    BookingEffect.NavigateBack -> navigator.pop()
                    BookingEffect.NavigateToSuccess -> { navigator.push(BookingSuccessScreen()) }
                    is BookingEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                    is BookingEffect.ShowSuccessMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        MedAIScaffold(
            title = state.doctor?.name ?: stringResource(Res.string.booking_screen_title),
            onBackClick = { viewModel.onEvent(BookingEvent.BackClicked) },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MedAITheme.colors.primary
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Doctor Summary could go here)
                Spacer(modifier = Modifier.height(dimensions.large))

                if (state.isLoadingDoctor) {
                    Box(modifier = Modifier.fillMaxWidth().height(dimensions.spacing64), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.onPrimary)
                    }
                }

                // White Body
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = dimensions.extraExtraLarge, topEnd = dimensions.extraExtraLarge))
                        .background(MedAITheme.colors.background)
                        .padding(dimensions.extraLarge)
                ) {
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MedAIText(stringResource(Res.string.upcoming_schedule_title), style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold))
                            MedAIText(
                                text = state.displayedMonth?.month?.name?.take(3) ?: stringResource(Res.string.month),
                                color = MedAITheme.colors.primary,
                                style = MedAITheme.textStyle.label.medium
                            )
                        }
                        Spacer(modifier = Modifier.height(dimensions.large))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Prev Month
                            IconButton(modifier = Modifier.size(dimensions.large), onClick = { viewModel.onEvent(BookingEvent.PrevMonthClicked) }) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = MedAITheme.colors.primary, modifier = Modifier.size(dimensions.extraLarge))
                            }

                            LazyRow(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(dimensions.medium),
                                contentPadding = PaddingValues(horizontal = dimensions.extraSmall)
                            ) {
                                items(state.calendarDays) { day ->
                                    MedAIDateCard(
                                        day = day.day,
                                        weekday = day.weekDay,
                                        isSelected = day.isSelected,
                                        // Note: For booking, we don't show the 'dot' (hasAppointment)
                                        // Unless we want to show doctor availability dots (advanced).
                                        onClick = { viewModel.onEvent(BookingEvent.DateSelected(day.fullDate)) }
                                    )
                                }
                            }

                            // Next Month
                            IconButton(modifier = Modifier.size(dimensions.large), onClick = { viewModel.onEvent(BookingEvent.NextMonthClicked) }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = MedAITheme.colors.primary, modifier = Modifier.size(dimensions.extraLarge))
                            }
                        }

                        Spacer(modifier = Modifier.height(dimensions.extraLarge))

                        // 2. Available Time
                        MedAIText(stringResource(Res.string.available_time_title), style = MedAITheme.textStyle.title.medium)
                        Spacer(modifier = Modifier.height(dimensions.large))

                        if (state.isLoadingSlots) {
                            Box(modifier = Modifier.fillMaxWidth().height(dimensions.spacing64 * 2), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MedAITheme.colors.primary)
                            }
                        }else if (state.slots.isEmpty()) {
                            MedAIText(stringResource(Res.string.no_slots_available), color = MedAITheme.colors.text.secondary, style = MedAITheme.textStyle.body.medium)
                        } else {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(dimensions.medium),
                                verticalArrangement = Arrangement.spacedBy(dimensions.medium),
                                maxItemsInEachRow = 4
                            ) {
                                state.slots.forEach { slot ->
                                    TimeSlotChip(
                                        time = slot.time,
                                        isSelected = slot.id == state.selectedSlotId,
                                        isAvailable = slot.isAvailable,
                                        onClick = { viewModel.onEvent(BookingEvent.SlotSelected(slot.id)) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(dimensions.extraLarge))

                        // 3. Patient Details Form
                        MedAIText(stringResource(Res.string.patient_details_title), style = MedAITheme.textStyle.title.medium)
                        Spacer(modifier = Modifier.height(dimensions.medium))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            PatientTypeChip(
                                text = stringResource(Res.string.yourself_label),
                                isSelected = state.bookingForSelf,
                                onClick = { viewModel.onEvent(BookingEvent.PatientTypeChanged(true)) }
                            )
                            Spacer(modifier = Modifier.width(dimensions.medium))
                            PatientTypeChip(
                                text = stringResource(Res.string.another_person_label),
                                isSelected = !state.bookingForSelf,
                                onClick = { viewModel.onEvent(BookingEvent.PatientTypeChanged(false)) }
                            )
                        }
                        Spacer(modifier = Modifier.height(dimensions.medium))
                        MedAIText(stringResource(Res.string.full_name_label), style = MedAITheme.textStyle.label.medium, color = MedAITheme.colors.text.secondary)
                        Spacer(modifier = Modifier.height(dimensions.small))
                        MedAiTextField(value = state.patientName, onValueChange = { viewModel.onEvent(BookingEvent.PatientNameChanges(it))}, placeholder = "Ahmed Gouda")

                        Spacer(modifier = Modifier.height(dimensions.extraExtraLarge))

                        if (state.isBooking) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        } else {
                            MedAIButton(
                                text = stringResource(Res.string.book_appointment_button),
                                onClick = { viewModel.onEvent(BookingEvent.BookClicked) },
                                variant = ButtonVariant.Primary,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = state.selectedSlotId != null
                            )
                        }

                        Spacer(modifier = Modifier.height(dimensions.spacing64))
                    }
                }
            }
        }
    }
}
