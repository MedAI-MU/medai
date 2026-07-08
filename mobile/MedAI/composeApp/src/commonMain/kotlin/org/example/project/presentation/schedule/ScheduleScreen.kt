package org.example.project.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import kotlinx.datetime.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.example.project.design_system.theme.MedAITheme
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.koin.core.parameter.parametersOf

class ScheduleScreen(private val doctorId: Int) : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<ScheduleViewModel> { parametersOf(doctorId) }
        val state by viewModel.state.collectAsState()

        // Refresh data when screen enters composition (e.g. navigating back)
        LaunchedEffect(Unit) {
            viewModel.onEvent(ScheduleEvent.RefreshCurrentTab)
        }

        // Show snackbar for action results
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ScheduleEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

        MedAIScaffold(
            topBar = {
                Column {
                    MedAiAppBar(
                        title = "Schedule Management",
                        centerTitle = false
                    )
                    // Tab Row
                    TabRow(
                        selectedTabIndex = state.selectedTab,
                        containerColor = MedAITheme.colors.surface,
                        contentColor = MedAITheme.colors.primary
                    ) {
                        Tab(
                            selected = state.selectedTab == 0,
                            onClick = { viewModel.onEvent(ScheduleEvent.OnTabSelected(0)) },
                            text = {
                                Text(
                                    "Templates",
                                    fontWeight = if (state.selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (state.selectedTab == 0) MedAITheme.colors.primary else MedAITheme.colors.text.secondary
                                )
                            },
                            icon = { Icon(Icons.Default.ContentCopy, contentDescription = null) }
                        )
                        Tab(
                            selected = state.selectedTab == 1,
                            onClick = { viewModel.onEvent(ScheduleEvent.OnTabSelected(1)) },
                            text = {
                                Text(
                                    "Schedule",
                                    fontWeight = if (state.selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (state.selectedTab == 1) MedAITheme.colors.primary else MedAITheme.colors.text.secondary
                                )
                            },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (state.selectedTab == 0) viewModel.onEvent(ScheduleEvent.ShowCreateTemplate)
                        else viewModel.onEvent(ScheduleEvent.ShowCreateSlot)
                    },
                    containerColor = MedAITheme.colors.primary,
                    contentColor = MedAITheme.colors.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (state.selectedTab) {
                    0 -> TemplatesTab(
                        state = state.templatesState,
                        searchQuery = state.templateSearchQuery,
                        onSearchQueryChanged = { viewModel.onEvent(ScheduleEvent.OnTemplateSearchQueryChanged(it)) },
                        onSearch = { viewModel.onEvent(ScheduleEvent.SearchTemplates) },
                        onEdit = { viewModel.onEvent(ScheduleEvent.ShowEditTemplate(it)) },
                        onDelete = { viewModel.onEvent(ScheduleEvent.DeleteTemplate(it)) },
                        onApply = { viewModel.onEvent(ScheduleEvent.ShowApplyTemplate(it)) },
                        onRetry = { viewModel.onEvent(ScheduleEvent.LoadTemplates(it)) }
                    )
                    1 -> SlotsTab(
                        scheduleState = state,
                        onDateSelected = { viewModel.onEvent(ScheduleEvent.DateSelected(it)) },
                        onPrevMonth = { viewModel.onEvent(ScheduleEvent.PrevMonthClicked) },
                        onNextMonth = { viewModel.onEvent(ScheduleEvent.NextMonthClicked) },
                        onDeleteSlot = { viewModel.onEvent(ScheduleEvent.DeleteSlot(it)) },
                        onRetry = { viewModel.onEvent(ScheduleEvent.LoadSlots()) },
                        onApplyTemplate = { viewModel.onEvent(ScheduleEvent.OnTabSelected(0)) },
                        onCreateSlot = { viewModel.onEvent(ScheduleEvent.ShowCreateSlot) }
                    )
                }

                // Loading overlay for actions
                if (state.isActionLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                }
            }
        }

        // Dialogs
        if (state.showCreateTemplateDialog) {
            CreateEditTemplateDialog(
                editingTemplate = state.editingTemplate,
                onDismiss = { viewModel.onEvent(ScheduleEvent.DismissCreateTemplateDialog) },
                onCreate = { name, slots -> viewModel.onEvent(ScheduleEvent.CreateTemplate(name, slots)) },
                onUpdate = { name, slots ->
                    state.editingTemplate?.let { viewModel.onEvent(ScheduleEvent.UpdateTemplate(it.id, name, slots)) }
                }
            )
        }

        if (state.showCreateSlotDialog) {
            CreateSlotDialog(
                initialDate = state.selectedDate?.toString() ?: "",
                onDismiss = { viewModel.onEvent(ScheduleEvent.DismissCreateSlotDialog) },
                onCreate = { date, start, end -> viewModel.onEvent(ScheduleEvent.CreateSlots(date, start, end)) }
            )
        }

        state.showApplyTemplateDialog?.let { template ->
            ApplyTemplateDialog(
                template = template,
                onDismiss = { viewModel.onEvent(ScheduleEvent.DismissApplyTemplateDialog) },
                onApply = { start, end -> viewModel.onEvent(ScheduleEvent.ApplyTemplate(template.id, start, end)) }
            )
        }
    }
}
