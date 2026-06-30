package org.example.project.presentation.doctor.records


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.core.parameter.parametersOf
import org.example.project.design_system.theme.MedAITheme

import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.component.textFields.MedAiDateTextField
import org.example.project.design_system.component.textFields.MedAiTextArea
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.domain.model.patient.AllergyEntity
import org.example.project.domain.model.patient.AllergyParams
import org.example.project.domain.model.patient.ChronicDiseaseEntity
import org.example.project.domain.model.patient.ChronicDiseaseParams
import org.example.project.domain.model.patient.EmergencyContactEntity
import org.example.project.domain.model.patient.EmergencyContactParams
import org.example.project.domain.model.patient.FamilyHistoryEntity
import org.example.project.domain.model.patient.FamilyHistoryParams
import org.example.project.domain.model.patient.FamilyRelation
import org.example.project.domain.model.patient.SurgeryEntity
import org.example.project.domain.model.patient.SurgeryParams
import org.example.project.presentation.shared.records.*

sealed class SheetType {
    object None : SheetType()
    data class BasicInfo(val weight: Double, val height: Double) : SheetType()

    // Allergies
    object AddAllergy : SheetType()
    data class EditAllergy(val allergy: AllergyEntity) : SheetType()

    // Diseases
    object AddDisease : SheetType()
    data class EditDisease(val disease: ChronicDiseaseEntity) : SheetType()

    // Surgeries
    object AddSurgery : SheetType()
    data class EditSurgery(val surgery: SurgeryEntity) : SheetType()

    // Family
    object AddFamily : SheetType()
    data class EditFamily(val history: FamilyHistoryEntity) : SheetType()

    // Emergency
    object AddEmergency : SheetType()
    data class EditEmergency(val contact: EmergencyContactEntity) : SheetType()
}

data class DoctorPatientRecordsScreen(val patientId: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel = getScreenModel<SharedMedicalRecordViewModel> { parametersOf(patientId) }
        val state by viewModel.state.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var currentSheet by remember { mutableStateOf<SheetType>(SheetType.None) }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is SharedMedicalRecordEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                    else -> {}
                }
            }
        }

        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Basic Info", "Allergies", "Diseases", "Surgeries", "Family", "Emergency")

        MedAIScaffold(
            title = "Patient Records",
            onBackClick = { navigator?.pop() },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MedAITheme.colors.primary)
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile.fullName,
                                        style = MedAITheme.textStyle.headline.medium,
                                        fontWeight = FontWeight.Bold,
                                        color = MedAITheme.colors.text.primary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { currentSheet = SheetType.BasicInfo(profile.weight, profile.height) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = MedAITheme.colors.primary)
                                    }
                                }
                                Text(
                                    text = "Gender: ${profile.gender} • Birth Date: ${profile.birthDate ?: "N/A"}",
                                    style = MedAITheme.textStyle.body.medium,
                                    color = MedAITheme.colors.text.secondary
                                )
                            }
                        }

                        // Tabs
                        androidx.compose.material3.ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = MedAITheme.colors.surface,
                            contentColor = MedAITheme.colors.primary,
                            edgePadding = 16.dp,
                            divider = {}
                        ) {
                            tabs.forEachIndexed { index, title ->
                                androidx.compose.material3.Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = { Text(title, style = MedAITheme.textStyle.label.medium) }
                                )
                            }
                        }

                        // Content
                        Box(modifier = Modifier.weight(1f)) {
                            when (selectedTabIndex) {
                                0 -> PatientBasicInfo(state, viewModel)
                                1 -> PatientAllergies(state, viewModel) { currentSheet = it }
                                2 -> PatientDiseases(state, viewModel) { currentSheet = it }
                                3 -> PatientSurgeries(state, viewModel) { currentSheet = it }
                                4 -> PatientFamilyHistory(state, viewModel) { currentSheet = it }
                                5 -> PatientEmergencyContacts(state, viewModel) { currentSheet = it }
                            }
                        }
                    }
                }

                // Bottom Sheet
                if (currentSheet != SheetType.None) {
                    ModalBottomSheet(
                        onDismissRequest = { currentSheet = SheetType.None },
                        sheetState = sheetState,
                        containerColor = MedAITheme.colors.background,
                        dragHandle = { androidx.compose.material3.BottomSheetDefaults.DragHandle(color = MedAITheme.colors.text.secondary.copy(alpha = 0.4f)) }
                    ) {
                        MedicalRecordForm(
                            sheetType = currentSheet,
                            onDismiss = { currentSheet = SheetType.None },
                            onEvent = { viewModel.onEvent(it) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalRecordForm(
    sheetType: SheetType,
    onDismiss: () -> Unit,
    onEvent: (SharedMedicalRecordEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(
            text = when (sheetType) {
                is SheetType.AddAllergy -> "Add Allergy"
                is SheetType.EditAllergy -> "Edit Allergy"
                is SheetType.AddDisease -> "Add Chronic Disease"
                is SheetType.EditDisease -> "Edit Chronic Disease"
                is SheetType.AddSurgery -> "Add Surgery"
                is SheetType.EditSurgery -> "Edit Surgery"
                is SheetType.AddFamily -> "Add Family History"
                is SheetType.EditFamily -> "Edit Family History"
                is SheetType.AddEmergency -> "Add Emergency Contact"
                is SheetType.EditEmergency -> "Edit Emergency Contact"
                is SheetType.BasicInfo -> "Update Basic Info"
                else -> ""
            },
            style = MedAITheme.textStyle.headline.small,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        when (sheetType) {
            is SheetType.BasicInfo -> {
                var weight by remember { mutableStateOf(sheetType.weight.toString()) }
                var height by remember { mutableStateOf(sheetType.height.toString()) }

                MedAiTextField(value = weight, onValueChange = { weight = it }, placeholder = "Weight (kg)")
                Spacer(modifier = Modifier.height(16.dp))
                MedAiTextField(value = height, onValueChange = { height = it }, placeholder = "Height (cm)")
                Spacer(modifier = Modifier.height(24.dp))
                MedAIButton(text = "Update", onClick = {
                    onEvent(SharedMedicalRecordEvent.UpdateBasicInfo(weight.toDoubleOrNull() ?: 0.0, height.toDoubleOrNull() ?: 0.0))
                    onDismiss()
                })
            }
            is SheetType.AddAllergy, is SheetType.EditAllergy -> {
                val allergy = (sheetType as? SheetType.EditAllergy)?.allergy
                var name by remember { mutableStateOf(allergy?.name ?: "") }
                var desc by remember { mutableStateOf(allergy?.symptoms ?: "") }

                MedAiTextField(value = name, onValueChange = { name = it }, placeholder = "Allergy Name")
                Spacer(modifier = Modifier.height(16.dp))
                MedAiTextArea(value = desc, onValueChange = { desc = it }, placeholder = "Description / Symptoms")
                Spacer(modifier = Modifier.height(24.dp))
                MedAIButton(text = if (allergy == null) "Add" else "Update", onClick = {
                    val params = AllergyParams(name = name, symptoms = desc)
                    if (allergy == null) onEvent(SharedMedicalRecordEvent.AddAllergy(params))
                    else onEvent(SharedMedicalRecordEvent.EditAllergy(allergy.id, params))
                    onDismiss()
                })
            }
            is SheetType.AddDisease, is SheetType.EditDisease -> {
                val disease = (sheetType as? SheetType.EditDisease)?.disease
                var name by remember { mutableStateOf(disease?.name ?: "") }
                var desc by remember { mutableStateOf(disease?.description ?: "") }
                var date by remember { mutableStateOf(disease?.diagnosisDate ?: "") }

                MedAiTextField(value = name, onValueChange = { name = it }, placeholder = "Disease Name")
                Spacer(modifier = Modifier.height(16.dp))
                MedAiTextArea(value = desc, onValueChange = { desc = it }, placeholder = "Description")
                Spacer(modifier = Modifier.height(16.dp))
                MedAiDateTextField(value = date, onValueChange = { date = it }, placeholder = "Diagnosis Date (DDMMYYYY)")
                Spacer(modifier = Modifier.height(24.dp))
                val isDateValid = date.isBlank() || date.filter { it.isDigit() }.length == 8
                val isValid = name.isNotBlank() && isDateValid

                if (!isValid) {
                    val errors = buildList {
                        if (name.isBlank()) add("Name is required.")
                        if (!isDateValid) add("Date must be exactly 8 digits (DDMMYYYY).")
                    }.joinToString("\n")
                    androidx.compose.material3.Text(text = errors, color = androidx.compose.material3.MaterialTheme.colorScheme.error, style = MedAITheme.textStyle.label.small, modifier = Modifier.padding(bottom = 8.dp))
                }

                MedAIButton(text = if (disease == null) "Add" else "Update", enabled = isValid, onClick = {
                    val params =
                        ChronicDiseaseParams(name = name, description = desc, diagnosisDate = date)
                    if (disease == null) onEvent(SharedMedicalRecordEvent.AddChronicDisease(params))
                    else onEvent(SharedMedicalRecordEvent.EditChronicDisease(disease.id, params))
                    onDismiss()
                })
            }
            is SheetType.AddSurgery, is SheetType.EditSurgery -> {
                val surgery = (sheetType as? SheetType.EditSurgery)?.surgery
                var name by remember { mutableStateOf(surgery?.name ?: "") }
                var desc by remember { mutableStateOf(surgery?.description ?: "") }
                var date by remember { mutableStateOf(surgery?.date ?: "") }

                MedAiTextField(value = name, onValueChange = { name = it }, placeholder = "Surgery Name")
                Spacer(modifier = Modifier.height(16.dp))
                MedAiTextArea(value = desc, onValueChange = { desc = it }, placeholder = "Details")
                Spacer(modifier = Modifier.height(16.dp))
                MedAiDateTextField(value = date, onValueChange = { date = it }, placeholder = "Date (DDMMYYYY)")
                Spacer(modifier = Modifier.height(24.dp))
                val isDateValid = date.filter { it.isDigit() }.length == 8
                val isValid = name.isNotBlank() && isDateValid

                if (!isValid) {
                    val errors = buildList {
                        if (name.isBlank()) add("Name is required.")
                        if (!isDateValid) add("Date must be exactly 8 digits (DDMMYYYY).")
                    }.joinToString("\n")
                    androidx.compose.material3.Text(text = errors, color = androidx.compose.material3.MaterialTheme.colorScheme.error, style = MedAITheme.textStyle.label.small, modifier = Modifier.padding(bottom = 8.dp))
                }

                MedAIButton(text = if (surgery == null) "Add" else "Update", enabled = isValid, onClick = {
                    val params = SurgeryParams(name = name, description = desc, date = date)
                    if (surgery == null) onEvent(SharedMedicalRecordEvent.AddSurgery(params))
                    else onEvent(SharedMedicalRecordEvent.EditSurgery(surgery.id, params))
                    onDismiss()
                })
            }
            is SheetType.AddFamily, is SheetType.EditFamily -> {
                val history = (sheetType as? SheetType.EditFamily)?.history
                var relation by remember { mutableStateOf(history?.relation ?: FamilyRelation.Father) }
                var condition by remember { mutableStateOf(history?.condition ?: "") }
                var notes by remember { mutableStateOf(history?.notes ?: "") }
                var relationExpanded by remember { mutableStateOf(false) }

                Text("Relation", style = MedAITheme.textStyle.label.medium, modifier = Modifier.padding(bottom = 8.dp))
                ExposedDropdownMenuBox(
                    expanded = relationExpanded,
                    onExpandedChange = { relationExpanded = it }
                ) {
                    MedAiTextField(
                        value = relation.label,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = "Select Relation",
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = relationExpanded,
                        onDismissRequest = { relationExpanded = false }
                    ) {
                        FamilyRelation.entries.filter { it != FamilyRelation.Unknown }.forEach { rel ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(rel.label) },
                                onClick = {
                                    relation = rel
                                    relationExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                MedAiTextField(value = condition, onValueChange = { condition = it }, placeholder = "Condition")
                Spacer(modifier = Modifier.height(16.dp))
                MedAiTextArea(value = notes, onValueChange = { notes = it }, placeholder = "Notes")
                Spacer(modifier = Modifier.height(24.dp))
                MedAIButton(text = if (history == null) "Add" else "Update", onClick = {
                    val params = FamilyHistoryParams(
                        relation = relation,
                        condition = condition,
                        notes = notes
                    )
                    if (history == null) onEvent(SharedMedicalRecordEvent.AddFamilyHistory(params))
                    else onEvent(SharedMedicalRecordEvent.EditFamilyHistory(history.id, params))
                    onDismiss()
                })
            }
            is SheetType.AddEmergency, is SheetType.EditEmergency -> {
                val contact = (sheetType as? SheetType.EditEmergency)?.contact
                var name by remember { mutableStateOf(contact?.name ?: "") }
                var relation by remember {
                    mutableStateOf(
                        FamilyRelation.entries.find {
                            it.label.equals(contact?.relation, ignoreCase = true) || it.name.equals(contact?.relation, ignoreCase = true)
                        } ?: FamilyRelation.Other
                    )
                }
                var relationExpanded by remember { mutableStateOf(false) }
                var phone by remember { mutableStateOf(contact?.phoneNumber ?: "") }
                var email by remember { mutableStateOf(contact?.email ?: "") }
                var address by remember { mutableStateOf(contact?.address ?: "") }
                var notes by remember { mutableStateOf(contact?.notes ?: "") }

                MedAiTextField(value = name, onValueChange = { name = it }, placeholder = "Full Name")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Relation", style = MedAITheme.textStyle.label.medium, modifier = Modifier.padding(bottom = 8.dp))
                ExposedDropdownMenuBox(
                    expanded = relationExpanded,
                    onExpandedChange = { relationExpanded = it }
                ) {
                    MedAiTextField(
                        value = relation.label,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = "Select Relation",
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = relationExpanded,
                        onDismissRequest = { relationExpanded = false }
                    ) {
                        FamilyRelation.entries.filter { it != FamilyRelation.Unknown }.forEach { rel ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(rel.label) },
                                onClick = {
                                    relation = rel
                                    relationExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                MedAiTextField(value = phone, onValueChange = { phone = it }, placeholder = "Phone Number")
                Spacer(modifier = Modifier.height(16.dp))
                MedAiTextField(value = email, onValueChange = { email = it }, placeholder = "Email")
                Spacer(modifier = Modifier.height(16.dp))
                MedAiTextField(value = address, onValueChange = { address = it }, placeholder = "Address")
                Spacer(modifier = Modifier.height(16.dp))
                MedAiTextArea(value = notes, onValueChange = { notes = it }, placeholder = "Notes")
                Spacer(modifier = Modifier.height(24.dp))
                val isPhoneValid = phone.matches(Regex("^(010|011|012|015)\\d{8}$"))
                val isEmailValid = email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"))
                val isValid = name.isNotBlank() && address.isNotBlank() && isPhoneValid && isEmailValid

                if (!isValid) {
                    val errors = buildList {
                        if (name.isBlank()) add("Name is required.")
                        if (address.isBlank()) add("Address is required.")
                        if (phone.isNotBlank() && !isPhoneValid) add("Phone must start with 010/011/012/015 and be 11 digits.")
                        else if (phone.isBlank()) add("Phone is required.")
                        if (email.isNotBlank() && !isEmailValid) add("Invalid email format.")
                        else if (email.isBlank()) add("Email is required.")
                    }.joinToString("\n")
                    androidx.compose.material3.Text(text = errors, color = androidx.compose.material3.MaterialTheme.colorScheme.error, style = MedAITheme.textStyle.label.small, modifier = Modifier.padding(bottom = 8.dp))
                }

                MedAIButton(text = if (contact == null) "Add" else "Update", enabled = isValid, onClick = {
                    val params = EmergencyContactParams(
                        name = name,
                        relation = relation.name.lowercase(),
                        phoneNumber = phone,
                        email = email,
                        address = address,
                        notes = notes
                    )
                    if (contact == null) onEvent(SharedMedicalRecordEvent.AddEmergencyContact(params))
                    else onEvent(SharedMedicalRecordEvent.EditEmergencyContact(contact.id, params))
                    onDismiss()
                })
            }
            else -> {}
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PatientBasicInfo(state: SharedMedicalRecordState, viewModel: SharedMedicalRecordViewModel) {
    val profile = state.patientProfile ?: return
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        SectionHeader("Physical Metrics")
        Card(
            colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    VitalItem("Weight", "${profile.weight} kg")
                    VitalItem("Height", "${profile.height} cm")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    VitalItem("Blood Type", profile.bloodType.label)
                    VitalItem("Marital Status", profile.maritalStatus.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader("Contact Information")
        Card(
            colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow("Phone", profile.contactNumber)
                InfoRow("Email", profile.email)
                InfoRow("Address", profile.address)
            }
        }
    }
}

@Composable
fun PatientAllergies(state: SharedMedicalRecordState, viewModel: SharedMedicalRecordViewModel, onShowSheet: (SheetType) -> Unit) {
    MedicalListContent(
        items = state.patientProfile?.allergies ?: emptyList(),
        title = "Allergies",
        onAddClick = { onShowSheet(SheetType.AddAllergy) },
        onEditClick = { onShowSheet(SheetType.EditAllergy(it)) },
        onDeleteClick = { viewModel.onEvent(SharedMedicalRecordEvent.DeleteAllergy(it.id)) },
        itemContent = { allergy ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text(allergy.name, style = MedAITheme.textStyle.title.medium, fontWeight = FontWeight.Bold)
                Text(allergy.symptoms, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
            }
        }
    )
}

@Composable
fun PatientDiseases(state: SharedMedicalRecordState, viewModel: SharedMedicalRecordViewModel, onShowSheet: (SheetType) -> Unit) {
    MedicalListContent(
        items = state.patientProfile?.chronicDiseases ?: emptyList(),
        title = "Chronic Diseases",
        onAddClick = { onShowSheet(SheetType.AddDisease) },
        onEditClick = { onShowSheet(SheetType.EditDisease(it)) },
        onDeleteClick = { viewModel.onEvent(SharedMedicalRecordEvent.DeleteChronicDisease(it.id)) },
        itemContent = { disease ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text(disease.name, style = MedAITheme.textStyle.title.medium, fontWeight = FontWeight.Bold)
                disease.description?.let { Text(it, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary) }
                Text("Diagnosed: ${disease.diagnosisDate}", style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.primary)
            }
        }
    )
}

@Composable
fun PatientSurgeries(state: SharedMedicalRecordState, viewModel: SharedMedicalRecordViewModel, onShowSheet: (SheetType) -> Unit) {
    MedicalListContent(
        items = state.patientProfile?.surgeries ?: emptyList(),
        title = "Surgeries",
        onAddClick = { onShowSheet(SheetType.AddSurgery) },
        onEditClick = { onShowSheet(SheetType.EditSurgery(it)) },
        onDeleteClick = { viewModel.onEvent(SharedMedicalRecordEvent.DeleteSurgery(it.id)) },
        itemContent = { surgery ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text(surgery.name, style = MedAITheme.textStyle.title.medium, fontWeight = FontWeight.Bold)
                surgery.description?.let { Text(it, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary) }
                Text("Date: ${surgery.date}", style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.primary)
            }
        }
    )
}

@Composable
fun PatientFamilyHistory(state: SharedMedicalRecordState, viewModel: SharedMedicalRecordViewModel, onShowSheet: (SheetType) -> Unit) {
    MedicalListContent(
        items = state.patientProfile?.familyHistories ?: emptyList(),
        title = "Family History",
        onAddClick = { onShowSheet(SheetType.AddFamily) },
        onEditClick = { onShowSheet(SheetType.EditFamily(it)) },
        onDeleteClick = { viewModel.onEvent(SharedMedicalRecordEvent.DeleteFamilyHistory(it.id)) },
        itemContent = { history ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text("${history.relation.label}: ${history.condition}", style = MedAITheme.textStyle.title.medium, fontWeight = FontWeight.Bold)
                history.notes?.let { Text(it, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary) }
            }
        }
    )
}

@Composable
fun PatientEmergencyContacts(state: SharedMedicalRecordState, viewModel: SharedMedicalRecordViewModel, onShowSheet: (SheetType) -> Unit) {
    MedicalListContent(
        items = state.patientProfile?.emergencyContacts ?: emptyList(),
        title = "Emergency Contacts",
        onAddClick = { onShowSheet(SheetType.AddEmergency) },
        onEditClick = { onShowSheet(SheetType.EditEmergency(it)) },
        onDeleteClick = { viewModel.onEvent(SharedMedicalRecordEvent.DeleteEmergencyContact(it.id)) },
        itemContent = { contact ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text(contact.name, style = MedAITheme.textStyle.title.medium, fontWeight = FontWeight.Bold)
                Text("${contact.relation} Ã¢â‚¬Â¢ ${contact.phoneNumber}", style = MedAITheme.textStyle.body.small)
                if (contact.email.isNotEmpty()) Text(contact.email, style = MedAITheme.textStyle.body.small)
                Text(contact.address, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
            }
        }
    )
}

@Composable
fun <T> MedicalListContent(
    items: List<T>,
    title: String,
    onAddClick: () -> Unit,
    onEditClick: (T) -> Unit,
    onDeleteClick: (T) -> Unit,
    itemContent: @Composable (T) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MedAITheme.textStyle.title.large, fontWeight = FontWeight.Bold)
            IconButton(
                onClick = onAddClick,
                colors = IconButtonDefaults.iconButtonColors(containerColor = MedAITheme.colors.primary.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MedAITheme.colors.primary)
            }
        }

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No $title recorded", style = MedAITheme.textStyle.body.medium, color = MedAITheme.colors.text.secondary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            itemContent(item)
                            Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                IconButton(onClick = { onEditClick(item) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = { onDeleteClick(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = "$label: ", style = MedAITheme.textStyle.body.medium, fontWeight = FontWeight.Bold)
        Text(text = value.ifEmpty { "N/A" }, style = MedAITheme.textStyle.body.medium, color = MedAITheme.colors.text.secondary)
    }
}

@Composable
fun VitalItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MedAITheme.textStyle.title.medium, fontWeight = FontWeight.Bold, color = MedAITheme.colors.primary)
        Text(text = label, style = MedAITheme.textStyle.label.medium, color = MedAITheme.colors.text.secondary)
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
