package org.example.project.presentation.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AnalysisScreen(viewModel: AnalysisViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val result by viewModel.result.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState) {
            AnalysisState.IDLE -> {
                Button(onClick = { viewModel.toggleRecording() }) {
                    Text("Start Recording Symptoms")
                }
            }
            AnalysisState.RECORDING -> {
                Text("Recording... Speak now.", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.toggleRecording() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Stop & Analyze")
                }
            }
            AnalysisState.UPLOADING -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Uploading Audio...")
            }
            AnalysisState.ANALYZING -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("AI is Analyzing (Please Wait...)")
            }
            AnalysisState.SUCCESS -> {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Analysis Complete!", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Transcription:", style = MaterialTheme.typography.titleMedium)
                    Text(result?.transcription ?: "")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Clinical Analysis (JSON):", style = MaterialTheme.typography.titleMedium)
                    Text(result?.clinical_analysis.toString())
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.reset() }) {
                        Text("New Analysis")
                    }
                }
            }
            AnalysisState.ERROR -> {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.reset() }) {
                    Text("Try Again")
                }
            }
        }
    }
}
