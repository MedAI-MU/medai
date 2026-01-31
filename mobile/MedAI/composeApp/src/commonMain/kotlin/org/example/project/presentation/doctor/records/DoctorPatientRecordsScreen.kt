package org.example.project.presentation.doctor.records

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.scaffold.MedAIScaffold
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.core.parameter.parametersOf
import org.example.project.design_system.theme.MedAITheme

data class DoctorPatientRecordsScreen(val patientId: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel = getScreenModel<DoctorPatientRecordsViewModel> { parametersOf(patientId) }
        val state by viewModel.state.collectAsState()

        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Overview", "History", "Analyses")

        MedAIScaffold(
            title = "Patient Records",
            onBackClick = { navigator?.pop() }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    val profile = state.patientProfile
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Patient Header
                        if (profile != null) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = profile.fullName,
                                    style = MedAITheme.textStyle.headline.medium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Age: ${profile.age} • Blood: ${profile.bloodType.label}",
                                    style = MedAITheme.textStyle.body.medium,
                                    color = MedAITheme.colors.text.secondary
                                )
                            }
                        }

                        // Tabs
                        TabRow(selectedTabIndex = selectedTabIndex) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = { Text(title) }
                                )
                            }
                        }

                        // Content
                        when (selectedTabIndex) {
                            0 -> PatientOverview(state)
                            1 -> PatientHistory(state.medicalHistory)
                            2 -> PatientAnalyses(state.analyses)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientOverview(state: DoctorPatientRecordsState) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        SectionHeader("Vitals")
        Card(
            colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
                VitalItem("Weight", "${state.patientProfile?.weight ?: "-"} kg")
                VitalItem("Height", "${state.patientProfile?.height ?: "-"} cm")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("Allergies")
        if (state.allergies.isEmpty()) {
            Text("No allergies recorded", style = MedAITheme.textStyle.body.medium)
        } else {
            state.allergies.forEach {
                Text("• ${it.symptoms}", style = MedAITheme.textStyle.body.medium)
            }
        }
    }
}

@Composable
fun VitalItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MedAITheme.textStyle.title.medium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MedAITheme.textStyle.label.medium, color = MedAITheme.colors.text.secondary)
    }
}

@Composable
fun PatientHistory(history: List<org.example.project.domain.model.MedicalHistoryEntity>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        if (history.isEmpty()) {
            item { Text("No medical history available.") }
        } else {
            items(history) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.condition, style = MedAITheme.textStyle.title.small, fontWeight = FontWeight.Bold)
                        Text("Diagnosed: ${item.treatmentPlan}", style = MedAITheme.textStyle.label.small)
                        if (item.treatmentPlan != null) {
                            Text("Treatment: ${item.treatmentPlan}", style = MedAITheme.textStyle.body.small)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientAnalyses(analyses: List<org.example.project.domain.model.AnalysisEntity>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        if (analyses.isEmpty()) {
            item { Text("No analyses found.") }
        } else {
            items(analyses) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.type, style = MedAITheme.textStyle.title.small, fontWeight = FontWeight.Bold)
                            Text(item.date.toString(), style = MedAITheme.textStyle.label.small)
                        }
                        Text(item.status.name, style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MedAITheme.textStyle.title.medium,
        color = MedAITheme.colors.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
