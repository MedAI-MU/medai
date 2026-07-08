package org.example.project.presentation.secretary.queue

import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.secretary.QueueEntry
import org.example.project.domain.model.secretary.QueueStatus
import org.example.project.presentation.secretary.dashboard.SecretaryDashboardScreen
import org.example.project.design_system.theme.LocalDimensions

class QueueManagementScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<QueueManagementViewModel>()
        val state by viewModel.state.collectAsState()
        val dimensions = LocalDimensions.current

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is QueueManagementEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

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
            containerColor = MedAITheme.colors.background,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(dimensions.medium)
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(dimensions.small)
                    ) {
                        items(state.queues) { entry ->
                            QueueItem(
                                entry = entry,
                                onCheckIn = { viewModel.onEvent(QueueManagementEvent.CheckIn(entry.id)) }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun QueueItem(entry: QueueEntry, onCheckIn: () -> Unit) {
        val dimensions = LocalDimensions.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MedAITheme.colors.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(dimensions.radiusMedium))
                .padding(dimensions.medium)
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

            Spacer(modifier = Modifier.height(dimensions.small))

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
