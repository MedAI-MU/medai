package org.example.project.presentation.shared.diagnosis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.core.parameter.parametersOf
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.textFields.MedAiTextArea
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.diagnosis.Diagnosis
import org.example.project.domain.model.auth.UserRole

class DiagnosisListScreen(val patientId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel = getScreenModel<DiagnosisViewModel> { parametersOf(patientId) }
        MedAIScaffold(
            title = "Diagnoses",
            onBackClick = { navigator?.pop() }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                DiagnosisListContent(viewModel = viewModel, patientId = patientId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisListContent(viewModel: DiagnosisViewModel, patientId: String) {
    val state by viewModel.state.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var editingDiagnosis by remember { mutableStateOf<Diagnosis?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.onEvent(DiagnosisEvent.LoadDiagnoses)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MedAITheme.colors.primary
            )
        } else if (state.error != null) {
            Text(
                text = state.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Clinical Diagnoses",
                        style = MedAITheme.textStyle.title.large,
                        fontWeight = FontWeight.Bold
                    )

                    // Only Doctors can add diagnoses
                    if (state.userRole == UserRole.DOCTOR) {
                        IconButton(
                            onClick = {
                                editingDiagnosis = null
                                showSheet = true
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MedAITheme.colors.primary.copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Diagnosis", tint = MedAITheme.colors.primary)
                        }
                    }
                }

                if (state.diagnoses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No diagnoses recorded for this patient",
                            style = MedAITheme.textStyle.body.medium,
                            color = MedAITheme.colors.text.secondary
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.diagnoses) { diagnosis ->
                            DiagnosisCard(
                                diagnosis = diagnosis,
                                currentUserId = state.currentUserId,
                                userRole = state.userRole,
                                onEditClick = {
                                    editingDiagnosis = diagnosis
                                    showSheet = true
                                },
                                onDeleteClick = {
                                    viewModel.onEvent(DiagnosisEvent.DeleteDiagnosis(diagnosis.id.toString()))
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = MedAITheme.colors.background
            ) {
                DiagnosisForm(
                    diagnosis = editingDiagnosis,
                    latestAppointmentId = state.latestAppointmentId,
                    onDismiss = { showSheet = false },
                    onSubmit = { appointmentId, symptoms, summary ->
                        if (editingDiagnosis == null) {
                            viewModel.onEvent(DiagnosisEvent.CreateDiagnosis(appointmentId, symptoms, summary))
                        } else {
                            viewModel.onEvent(DiagnosisEvent.UpdateDiagnosis(editingDiagnosis!!.id.toString(), symptoms, summary))
                        }
                        showSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun DiagnosisCard(
    diagnosis: Diagnosis,
    currentUserId: String?,
    userRole: UserRole?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val canModify = userRole == UserRole.DOCTOR && diagnosis.doctorUserId.toString() == currentUserId

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp).padding(end = if (canModify) 80.dp else 16.dp)) {
                Text(
                    text = "Diagnosis #${diagnosis.id}",
                    style = MedAITheme.textStyle.title.medium,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Date: ${diagnosis.createdAt.take(10)}",
                    style = MedAITheme.textStyle.label.small,
                    color = MedAITheme.colors.text.secondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Symptoms:",
                    style = MedAITheme.textStyle.body.medium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = diagnosis.symptoms,
                    style = MedAITheme.textStyle.body.medium,
                    color = MedAITheme.colors.text.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Clinical Summary:",
                    style = MedAITheme.textStyle.body.medium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = diagnosis.summary,
                    style = MedAITheme.textStyle.body.medium,
                    color = MedAITheme.colors.text.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Attending Doctor: Doctor #${diagnosis.doctorUserId}",
                    style = MedAITheme.textStyle.label.medium,
                    color = MedAITheme.colors.primary
                )
            }

            if (canModify) {
                Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosisForm(
    diagnosis: Diagnosis?,
    latestAppointmentId: Int?,
    onDismiss: () -> Unit,
    onSubmit: (appointmentId: Int?, symptoms: String, summary: String) -> Unit
) {
    var symptoms by remember { mutableStateOf(diagnosis?.symptoms ?: "") }
    var summary by remember { mutableStateOf(diagnosis?.summary ?: "") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (diagnosis == null) "Create Diagnosis" else "Edit Diagnosis",
            style = MedAITheme.textStyle.headline.small,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        MedAiTextArea(
            value = symptoms,
            onValueChange = { symptoms = it },
            placeholder = "Symptoms"
        )
        Spacer(modifier = Modifier.height(16.dp))

        MedAiTextArea(
            value = summary,
            onValueChange = { summary = it },
            placeholder = "Summary"
        )
        Spacer(modifier = Modifier.height(24.dp))

        val isValid = symptoms.isNotBlank() && summary.isNotBlank()

        MedAIButton(
            text = if (diagnosis == null) "Create" else "Save Changes",
            enabled = isValid,
            onClick = {
                onSubmit(latestAppointmentId, symptoms, summary)
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}
