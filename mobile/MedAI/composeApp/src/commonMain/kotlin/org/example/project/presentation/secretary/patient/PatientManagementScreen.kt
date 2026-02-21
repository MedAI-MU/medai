package org.example.project.presentation.secretary.patient

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.launch
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.Patient
import org.example.project.domain.usecase.secretary.CreatePatientUseCase
import org.example.project.domain.usecase.secretary.GetAllPatientsUseCase
import org.example.project.presentation.doctor.records.DoctorPatientRecordsScreen

class PatientManagementScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<PatientManagementViewModel>()
        val state by viewModel.state.collectAsState()

        // Local state for search
        var searchQuery by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Patients", style = MedAITheme.textStyle.headline.small) },
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* TODO: Navigate to Add Patient Dialog or Screen */ },
                    containerColor = MedAITheme.colors.primary,
                    contentColor = MedAITheme.colors.text.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Patient")
                }
            },
            containerColor = MedAITheme.colors.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Search Bar
                MedAiTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        // Filter logic could go here or in VM
                    },
                    placeholder = "Search by name or phone...",
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MedAITheme.colors.primary) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Simple client-side filter for now
                        val filteredPatients = state.patients.filter {
                            it.fullName.contains(searchQuery, ignoreCase = true) || it.contactNumber.contains(searchQuery)
                        }

                        items(filteredPatients) { patient ->
                            PatientItem(patient, onClick = { navigator.push(DoctorPatientRecordsScreen(patient.id)) })
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PatientItem(patient: Patient, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MedAITheme.colors.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = patient.fullName, style = MedAITheme.textStyle.body.large, fontWeight = FontWeight.Bold, color = MedAITheme.colors.text.primary)
                Text(text = "Age: ${patient.age} • ${patient.gender}", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                Text(text = patient.contactNumber, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
            }
        }
    }
}

class PatientManagementViewModel(
    private val getAllPatientsUseCase: GetAllPatientsUseCase,
    private val createPatientUseCase: CreatePatientUseCase
) : ScreenModel {

    data class State(
        val patients: List<Patient> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        loadPatients()
    }

    private fun loadPatients() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val patients = getAllPatientsUseCase()
                _state.value = _state.value.copy(patients = patients, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}
