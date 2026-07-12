package org.example.project.presentation.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.AutoAwesome
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
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.categories
import medai.composeapp.generated.resources.specialties
import medai.composeapp.generated.resources.upcoming_schedule
import org.example.project.design_system.component.dayPicker.MedAIDateCard
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.home.CategoryType
import org.example.project.presentation.doctorsScreen.DoctorsScreen
import org.example.project.presentation.homeScreen.component.CategoryItem
import org.example.project.presentation.homeScreen.component.HomeAppointmentCard
import org.example.project.presentation.homeScreen.component.SpecialtyItem
import org.example.project.design_system.component.header.SectionHeader
import org.example.project.design_system.component.topBar.HomeTopBar
import org.example.project.presentation.notificationScreen.NotificationScreen
import org.example.project.presentation.recordScreen.RecordsDashboardScreen
import org.example.project.presentation.specialtiesScreen.SpecialtiesScreen
import org.jetbrains.compose.resources.stringResource
import org.example.project.presentation.appointmentScreen.AppointmentDetailScreen

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HomeViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when(effect) {
                    is HomeEffect.NavigateToAppointmentDetails -> {
                        val rootNavigator = navigator.parent ?: navigator
                        rootNavigator.push(AppointmentDetailScreen(effect.appointmentId))
                    }
                    is HomeEffect.NavigateToCategory -> {
                        when (effect.category.type) {
                            CategoryType.FAVORITE -> snackbarHostState.showSnackbar(effect.category.id)
                            CategoryType.DOCTORS -> {
                                val rootNavigator = navigator.parent ?: navigator
                                rootNavigator.push(DoctorsScreen(
                                    specialtyId = null,
                                    specialtyName = null
                                ))
                            }
                            CategoryType.PHARMACY -> snackbarHostState.showSnackbar(effect.category.id)
                            CategoryType.SPECIALTIES -> {
                                val rootNavigator = navigator.parent ?: navigator
                                rootNavigator.push(SpecialtiesScreen())
                            }
                            CategoryType.RECORDS -> {
                                val rootNavigator = navigator.parent ?: navigator
                                rootNavigator.push(RecordsDashboardScreen())
                            }
                            CategoryType.UNKNOWN -> snackbarHostState.showSnackbar(effect.category.id)
                        }
                    }
                    is HomeEffect.NavigateToDoctorDetails -> {
                        // navigator.push(DoctorDetailScreen(effect.doctorId))
                    }
                    is HomeEffect.NavigateToSpecialty -> {
                        val screen = DoctorsScreen(specialtyId = effect.specialtyId, effect.title)
                        navigator.parent?.push(screen) ?: navigator.push(screen)
                    }
                    HomeEffect.NavigateToAllCategories -> {
                        // navigator.push(AllCategoriesScreen())
                    }
                    HomeEffect.NavigateToFullSchedule -> {
                        // navigator.push(ScheduleScreen())
                    }
                    HomeEffect.NavigateToAllSpecialties -> {
                        val rootNavigator = navigator.parent ?: navigator
                        rootNavigator.push(SpecialtiesScreen())
                    }
                    HomeEffect.NavigateToNotifications -> {
                        val rootNavigator = navigator.parent ?: navigator
                        rootNavigator.push(NotificationScreen())
                    }
                    HomeEffect.NavigateToSearch -> {
                    }
                    HomeEffect.NavigateToSettings -> {
                    }
                    is HomeEffect.ShowError -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }

                    is HomeEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        MedAIScaffold(
            topBar = {
                HomeTopBar(
                    userName = state.userName,
                    avatarUrl = state.avatarUrl,
                    onNotificationClick = { viewModel.onEvent(HomeEvent.NotificationsClicked) },
                    onSettingsClick = { viewModel.onEvent(HomeEvent.SettingsClicked) },
                    onSearchClick = { viewModel.onEvent(HomeEvent.SearchClicked) }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        ) { padding ->
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MedAITheme.colors.primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        //.padding(bottom = 60.dp) // Space for bottom nav
                ) {
                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

                    // 1. Categories Section
                    SectionHeader(
                        title = stringResource(Res.string.categories),
                        onSeeAllClick = { viewModel.onEvent(HomeEvent.SeeAllCategoriesClicked) }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = MedAITheme.dimensions.extraLarge),
                        horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.extraLarge)
                    ) {
                        items(state.categories) { category ->
                            CategoryItem(
                                category = category,
                                onClick = { viewModel.onEvent(HomeEvent.CategoryClicked(category)) }
                            )
                        }
                        item {
                            CategoryReportAIItem(
                                onClick = {
                                    val rootNavigator = navigator.parent ?: navigator
                                    rootNavigator.push(org.example.project.presentation.reportAnalysis.ReportAnalysisScreen())
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MedAITheme.colors.primary.copy(alpha = 0.5f))
                            .padding(vertical = MedAITheme.dimensions.extraLarge)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MedAITheme.dimensions.extraLarge, vertical = MedAITheme.dimensions.small),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MedAIText(
                                text = stringResource(Res.string.upcoming_schedule),
                                style = MedAITheme.textStyle.title.large.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            MedAIText(
                                text = state.displayedMonth?.month?.name?.take(3) ?: "Month",
                                style = MedAITheme.textStyle.label.medium,
                                color = Color.White,
                                modifier = Modifier.clickable { /* TODO show month Picker */ }
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = MedAITheme.dimensions.extraLarge),
                            color = Color.White.copy(alpha = 0.5f),
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

                        // Date Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.onEvent(HomeEvent.PreviousMonthClicked) }) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(MedAITheme.dimensions.iconLarge))
                            }

                            LazyRow(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.medium),
                                contentPadding = PaddingValues(horizontal = MedAITheme.dimensions.small)
                            ) {
                                items(state.calendarDays) { dateModel ->
                                    MedAIDateCard(
                                        day = dateModel.day,
                                        weekday = dateModel.weekDay,
                                        isSelected = dateModel.isSelected,
                                        hasAppointment = dateModel.hasAppointment,
                                        onClick = {
                                            viewModel.onEvent(HomeEvent.DateSelected(dateModel.fullDate))
                                        }
                                    )
                                }
                            }

                            IconButton(onClick = { viewModel.onEvent(HomeEvent.NextMonthClicked) }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(MedAITheme.dimensions.iconLarge))
                            }
                        }

                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

                        if (state.filteredAppointments.isNotEmpty()) {
                            HomeAppointmentCard(
                                appointments = state.filteredAppointments,
                                onItemClick = { id -> viewModel.onEvent(HomeEvent.AppointmentClicked(id)) },
                                modifier = Modifier.padding(horizontal = MedAITheme.dimensions.extraLarge)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = MedAITheme.dimensions.extraLarge)
                                    .clip(RoundedCornerShape(MedAITheme.dimensions.radiusExtraLarge))
                                    .background(Color.White.copy(alpha = 0.15f)) // Glass-like effect
                                    .padding(vertical = MedAITheme.dimensions.extraExtraLarge),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    MedAIText(
                                        text = "No appointments for this date",
                                        style = MedAITheme.textStyle.title.medium,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                    // 3. Specialties Section
                    SectionHeader(
                        title = stringResource(Res.string.specialties),
                        onSeeAllClick = { viewModel.onEvent(HomeEvent.SeeAllSpecialtiesClicked) }
                    )


                    Column(modifier = Modifier.padding(horizontal = MedAITheme.dimensions.extraLarge)) {
                        val chunks = state.specialties.chunked(3)
                        chunks.forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.large)
                            ) {
                                rowItems.forEach { specialty ->
                                    SpecialtyItem(
                                        specialty = specialty,
                                        onClick = { viewModel.onEvent(HomeEvent.SpecialtyClicked(specialty.id)) },
                                        modifier = Modifier.weight(1f).height(120.dp)
                                    )
                                }
                                // Fill empty space if row is incomplete
                                if (rowItems.size < 3) {
                                    Spacer(modifier = Modifier.weight((3 - rowItems.size).toFloat()))
                                }
                            }
                            Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryReportAIItem(
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MedAITheme.colors.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        MedAIText(
            text = "Report AI",
            style = MedAITheme.textStyle.label.medium,
            color = MedAITheme.colors.primary
        )
    }
}
