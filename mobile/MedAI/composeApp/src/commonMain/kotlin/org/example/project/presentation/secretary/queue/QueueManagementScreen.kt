package org.example.project.presentation.secretary.queue

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.secretary.QueueEntry
import org.example.project.domain.model.secretary.QueueStatus
import org.example.project.domain.usecase.secretary.CheckInPatientUseCase
import org.example.project.domain.usecase.secretary.GetAllQueuesUseCase
import org.example.project.presentation.secretary.dashboard.SecretaryDashboardScreen

class QueueManagementScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<QueueManagementViewModel>()
        val state by viewModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Queue Management", style = MedAITheme.textStyle.headline.small) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MedAITheme.colors.primary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MedAITheme.colors.background,
                        titleContentColor = MedAITheme.colors.text.primary
                    )
                )
            },
            containerColor = MedAITheme.colors.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.queues) { entry ->
                            QueueItem(
                                entry = entry,
                                onCheckIn = { viewModel.checkIn(entry.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun QueueItem(entry: QueueEntry, onCheckIn: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MedAITheme.colors.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = entry.patientName, style = MedAITheme.textStyle.body.large, fontWeight = FontWeight.Bold, color = MedAITheme.colors.text.primary)
                    Text(text = entry.doctorName, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                    Text(text = "Time: ${entry.appointmentTime.takeLast(8)}", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                }

                SecretaryDashboardScreen().QueueStatusBadge(entry.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (entry.status == QueueStatus.WAITING || entry.status == QueueStatus.CANCELLED) { // Simplified logic
                 MedAIButton(
                    text = "Check In",
                    onClick = onCheckIn,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Secondary
                )
            }
        }
    }
}

class QueueManagementViewModel(
    private val getAllQueuesUseCase: GetAllQueuesUseCase,
    private val checkInPatientUseCase: CheckInPatientUseCase
) : ScreenModel {

    data class State(
        val queues: List<QueueEntry> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        loadQueues()
    }

    private fun loadQueues() {
        getAllQueuesUseCase()
            .onEach { queues ->
                _state.value = _state.value.copy(queues = queues)
            }
            .launchIn(screenModelScope)
    }

    fun checkIn(appointmentId: String) {
        screenModelScope.launch {
            try {
                checkInPatientUseCase(appointmentId)
                // Flow should auto-update
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}
