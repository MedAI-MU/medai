package org.example.project.presentation.medicalReportsScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import org.example.project.domain.model.MedicalReport
import org.example.project.domain.model.CreateMedicalReportRequest
import cafe.adriel.voyager.core.screen.Screen

data class MedicalReportsScreen(
    val patientId: Int,
    val token: String,
    val httpClient: HttpClient
) : Screen {

@Composable
override fun Content() {
    var reports by remember { mutableStateOf<List<MedicalReport>>(emptyList()) }
    var scanData by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(patientId) {
        coroutineScope.launch {
            try {
                isLoading = true
                val response: HttpResponse = httpClient.get("http://10.0.2.2:3000/patients/$patientId/reports") {
                    header("Authorization", "Bearer $token")
                }
                reports = response.body()
            } catch (e: Exception) {
                errorMessage = "Failed to load reports: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "AI Medical Reports", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Create Report Form
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Generate New Report", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = scanData,
                    onValueChange = { scanData = it },
                    label = { Text("Scan Image URL or Text") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response: HttpResponse = httpClient.post("http://10.0.2.2:3000/patients/$patientId/reports") {
                                    header("Authorization", "Bearer $token")
                                    contentType(ContentType.Application.Json)
                                    setBody(CreateMedicalReportRequest(scanData = scanData))
                                }
                                val newReport: MedicalReport = response.body()
                                reports = listOf(newReport) + reports
                                scanData = ""
                            } catch (e: Exception) {
                                errorMessage = "Failed to generate report: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = scanData.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isLoading) "Generating..." else "Generate AI Report")
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Past Reports", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading && reports.isEmpty()) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(reports) { report ->
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
