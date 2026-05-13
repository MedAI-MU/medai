package org.example.project.presentation.patientDirectory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Add
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.textFields.MedAISearchBar
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.patient.Patient


import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

import org.example.project.presentation.doctor.records.DoctorPatientRecordsScreen
// import org.example.project.presentation.secretary.patient.AddPatientScreen // Creating this later

class PatientsDirectoryScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<PatientsDirectoryViewModel>()

        PatientsDirectoryContent(
            viewModel = viewModel,
            onNavigateBack = { navigator.pop() },
            onNavigateToAddPatient = {
                // navigator.push(AddPatientScreen())
                // TODO: Implement Add Patient Navigation
            },
            onNavigateToPatientDetails = { patientId ->
                 navigator.push(DoctorPatientRecordsScreen(patientId))
            }
        )
    }
}

@Composable
fun PatientsDirectoryContent(
    viewModel: PatientsDirectoryViewModel,
    onNavigateToPatientDetails: (String) -> Unit,
    onNavigateToAddPatient: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PatientsDirectoryEffect.NavigateToPatientDetails -> {
                    onNavigateToPatientDetails(effect.patientId)
                }
                is PatientsDirectoryEffect.NavigateToAddPatient -> {
                    onNavigateToAddPatient()
                }
                is PatientsDirectoryEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    MedAIScaffold(
        title = "Patients Directory",
        onBackClick = onNavigateBack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = { viewModel.onEvent(PatientsDirectoryEvent.OnAddPatientClicked) },
                containerColor = MedAITheme.colors.primary,
                contentColor = androidx.compose.ui.graphics.Color.White
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Add,
                    contentDescription = "Add Patient"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            MedAISearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.onEvent(PatientsDirectoryEvent.OnSearchQueryChanged(it)) },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MedAITheme.colors.primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.error ?: "Unknown Error",
                        color = MedAITheme.colors.neutral,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.filteredPatients) { patient ->
                        PatientItem(
                            patient = patient,
                            onClick = { viewModel.onEvent(PatientsDirectoryEvent.OnPatientClicked(patient)) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PatientItem(
    patient: Patient,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MedAITheme.colors.surface)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = patient.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.text.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "ID: ${patient.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MedAITheme.colors.text.secondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Badge(text = "Blood: ${patient.bloodType.label}", color = MedAITheme.colors.primary.copy(alpha = 0.1f), textColor = MedAITheme.colors.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Badge(text = "Age: ${patient.age}", color = MedAITheme.colors.secondary.copy(alpha = 0.1f), textColor = MedAITheme.colors.secondary)
            }
        }
    }
}

@Composable
fun Badge(
    text: String,
    color: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}
