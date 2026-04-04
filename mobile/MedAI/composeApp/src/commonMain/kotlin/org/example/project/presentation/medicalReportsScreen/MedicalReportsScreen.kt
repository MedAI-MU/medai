package org.example.project.presentation.medicalReportsScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import kotlinx.coroutines.flow.collectLatest

data class MedicalReportsScreen(
    val patientId: Int,
    val token: String
) : Screen {

    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MedicalReportsViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(patientId) {
            viewModel.onEvent(MedicalReportsEvent.Init(patientId, token))
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is MedicalReportsEffect.ShowToast -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
                Text(text = "AI Medical Reports", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // Create Report Form
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Generate New Report", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = state.scanDataInput,
                            onValueChange = { viewModel.onEvent(MedicalReportsEvent.OnScanDataChanged(it)) },
                            label = { Text("Scan Image URL or Text") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.onEvent(MedicalReportsEvent.GenerateReportClicked) },
                            enabled = state.scanDataInput.isNotBlank() && !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (state.isLoading) "Generating..." else "Generate AI Report")
                        }
                    }
                }

                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Past Reports", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                if (state.isLoading && state.reports.isEmpty()) {
                    CircularProgressIndicator()
                } else {
                    LazyColumn {
                        items(state.reports) { report ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text(text = report.createdAt, style = MaterialTheme.typography.bodySmall)
                                        Text(text = report.status, style = MaterialTheme.typography.labelSmall)
                                    }
                                    if (report.scanImageUrl != null) {
                                        Text(
                                            text = "Scan Ref: ${report.scanImageUrl}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = report.generatedReport, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
