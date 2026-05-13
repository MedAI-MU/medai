package org.example.project.presentation.recordScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.component.textFields.MedAiDateTextField
import org.example.project.design_system.component.textFields.MedAiTextArea
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.patient.AllergyEntity
import org.example.project.domain.model.patient.AllergyParams
import org.example.project.domain.model.medical_record.AnalysisStatus
import org.example.project.domain.model.patient.BloodType
import org.example.project.domain.model.patient.ChronicDiseaseEntity
import org.example.project.domain.model.patient.ChronicDiseaseParams
import org.example.project.domain.model.patient.EmergencyContactEntity
import org.example.project.domain.model.patient.EmergencyContactParams
import org.example.project.domain.model.patient.FamilyHistoryEntity
import org.example.project.domain.model.patient.FamilyHistoryParams
import org.example.project.domain.model.patient.FamilyRelation
import org.example.project.domain.model.patient.MaritalStatus
import org.example.project.domain.model.patient.Patient
import org.example.project.domain.model.patient.SurgeryEntity
import org.example.project.domain.model.patient.SurgeryParams
import org.example.project.domain.model.patient.UpdatePatientParams

// --- Sheet Type Sealed Class (patient-specific) ---
sealed class PatientSheetType {
    object None : PatientSheetType()
    object AddAllergy : PatientSheetType()
    data class EditAllergy(val allergy: AllergyEntity) : PatientSheetType()
    object AddDisease : PatientSheetType()
    data class EditDisease(val disease: ChronicDiseaseEntity) : PatientSheetType()
    object AddSurgery : PatientSheetType()
    data class EditSurgery(val surgery: SurgeryEntity) : PatientSheetType()
    object AddFamily : PatientSheetType()
    data class EditFamily(val history: FamilyHistoryEntity) : PatientSheetType()
    object AddEmergency : PatientSheetType()
    data class EditEmergency(val contact: EmergencyContactEntity) : PatientSheetType()
    object EditProfile : PatientSheetType()
}

class RecordsEmptyScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        MedAIScaffold(title = "Medical Records") {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MedAITheme.colors.text.secondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                MedAIText(
                    text = "You Have Not Added Any Medical Records Yet",
                    style = MedAITheme.textStyle.title.medium,
                    color = MedAITheme.colors.text.secondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. Dashboard Screen (Main Entry)
// -----------------------------------------------------------------------------
class RecordsDashboardScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<MedicalRecordViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var currentSheet by remember { mutableStateOf<PatientSheetType>(PatientSheetType.None) }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { if (it is MedicalRecordEffect.ShowSnackbar) snackbarHostState.showSnackbar(it.message) }
        }

        MedAIScaffold(title = "Medical Records", snackbarHost = { SnackbarHost(snackbarHostState) }) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MedAITheme.colors.primary)
                }
            } else {
                DashboardContent(state, navigator, onEditProfile = { currentSheet = PatientSheetType.EditProfile })
            }
        }

        if (currentSheet == PatientSheetType.EditProfile) {
            ModalBottomSheet(
                onDismissRequest = { currentSheet = PatientSheetType.None },
                sheetState = sheetState,
                containerColor = MedAITheme.colors.background
            ) {
                PatientProfileForm(
                    profile = state.patientProfile,
                    onDismiss = { currentSheet = PatientSheetType.None },
                    onEvent = { viewModel.onEvent(it) }
                )
            }
        }
    }

    @Composable
    fun DashboardContent(state: MedicalRecordState, navigator: cafe.adriel.voyager.navigator.Navigator, onEditProfile: () -> Unit) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            state.patientProfile?.let { UserHeader(it, onEditClick = onEditProfile) }
            Spacer(modifier = Modifier.height(24.dp))

            val items = listOf(
                DashboardItem("Allergies", Icons.Default.LocalHospital, Color(0xFFFFCDD2)) { navigator.push(AllergiesScreen()) },
                DashboardItem("Diseases", Icons.Default.Science, Color(0xFFE1BEE7)) { navigator.push(DiseasesScreen()) },
                DashboardItem("Surgeries", Icons.Default.Vaccines, Color(0xFFC8E6C9)) { navigator.push(SurgeriesScreen()) },
                DashboardItem("Family", Icons.Default.History, Color(0xFFBBDEFB)) { navigator.push(FamilyHistoryScreen()) },
                DashboardItem("Emergency", Icons.Default.ContactPhone, Color(0xFFFFF9C4)) { navigator.push(EmergencyContactsScreen()) },
                DashboardItem("Analyses", Icons.Default.ListAlt, Color(0xFFD1C4E9)) { navigator.push(AnalysesScreen()) }
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { item -> DashboardCard(item) }
            }
        }
    }
}

data class DashboardItem(val title: String, val icon: ImageVector, val color: Color, val onClick: () -> Unit)

@Composable
fun DashboardCard(item: DashboardItem) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable { item.onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = item.color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(item.icon, null, tint = item.color.copy(alpha = 1f), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            MedAIText(item.title, style = MedAITheme.textStyle.title.medium, color = MedAITheme.colors.text.primary)
        }
    }
}

@Composable
fun UserHeader(profile: Patient, onEditClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MedAITheme.colors.primary.copy(alpha = 0.1f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            MedAIText(profile.fullName, style = MedAITheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(4.dp))
            MedAIText("Blood Type: ${profile.bloodType.label}", style = MedAITheme.textStyle.body.medium)
            MedAIText("Height: ${profile.height} cm", style = MedAITheme.textStyle.body.medium)
            MedAIText("Weight: ${profile.weight} kg", style = MedAITheme.textStyle.body.medium)
            MedAIText("Status: ${profile.maritalStatus.name}", style = MedAITheme.textStyle.body.medium)
        }
        IconButton(onClick = onEditClick) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Edit Profile",
                tint = MedAITheme.colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileForm(
    profile: Patient?,
    onDismiss: () -> Unit,
    onEvent: (MedicalRecordEvent) -> Unit
) {
    var height by remember { mutableStateOf(profile?.height?.toString() ?: "") }
    var weight by remember { mutableStateOf(profile?.weight?.toString() ?: "") }
    var selectedBloodType by remember { mutableStateOf(profile?.bloodType ?: BloodType.UNKNOWN) }
    var selectedMaritalStatus by remember { mutableStateOf(profile?.maritalStatus ?: MaritalStatus.Single) }
    var bloodTypeExpanded by remember { mutableStateOf(false) }
    var maritalStatusExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        MedAIText(
            "Edit Profile",
            style = MedAITheme.textStyle.headline.small,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        MedAiTextField(value = height, onValueChange = { height = it }, placeholder = "Height (cm)")
        Spacer(modifier = Modifier.height(16.dp))
        MedAiTextField(value = weight, onValueChange = { weight = it }, placeholder = "Weight (kg)")
        Spacer(modifier = Modifier.height(16.dp))

        // Blood Type Dropdown
        MedAIText("Blood Type", style = MedAITheme.textStyle.label.medium, modifier = Modifier.padding(bottom = 8.dp))
        ExposedDropdownMenuBox(
            expanded = bloodTypeExpanded,
            onExpandedChange = { bloodTypeExpanded = it }
        ) {
            MedAiTextField(
                value = selectedBloodType.label,
                onValueChange = {},
                readOnly = true,
                placeholder = "Select Blood Type",
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = bloodTypeExpanded,
                onDismissRequest = { bloodTypeExpanded = false }
            ) {
                BloodType.entries.filter { it != BloodType.UNKNOWN }.forEach { type ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(type.label) },
                        onClick = {
                            selectedBloodType = type
                            bloodTypeExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Marital Status Dropdown
        MedAIText("Marital Status", style = MedAITheme.textStyle.label.medium, modifier = Modifier.padding(bottom = 8.dp))
        ExposedDropdownMenuBox(
            expanded = maritalStatusExpanded,
            onExpandedChange = { maritalStatusExpanded = it }
        ) {
            MedAiTextField(
                value = selectedMaritalStatus.name,
                onValueChange = {},
                readOnly = true,
                placeholder = "Select Marital Status",
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = maritalStatusExpanded,
                onDismissRequest = { maritalStatusExpanded = false }
            ) {
                MaritalStatus.entries.forEach { status ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(status.name) },
                        onClick = {
                            selectedMaritalStatus = status
                            maritalStatusExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        MedAIButton(
            text = "Save Changes",
            onClick = {
                val params = UpdatePatientParams(
                    height = height.toDoubleOrNull(),
                    weight = weight.toDoubleOrNull(),
                    bloodType = selectedBloodType,
                    maritalStatus = selectedMaritalStatus
                )
                onEvent(MedicalRecordEvent.UpdatePatientInfo(params))
                onDismiss()
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun RecordEmptyState(label: String, onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(MedAITheme.colors.primary.copy(alpha = 0.1f))
                .padding(16.dp),
            tint = MedAITheme.colors.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        MedAIText(
            text = label,
            style = MedAITheme.textStyle.title.medium,
            color = MedAITheme.colors.text.secondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        MedAIText(
            text = "Tap the button below to add your first entry.",
            style = MedAITheme.textStyle.body.medium,
            color = MedAITheme.colors.text.secondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        MedAIButton(text = "Add First Record", onClick = onAddClick)
    }
}

// -----------------------------------------------------------------------------
// 3. Sub-Screens with Add/Edit/Delete support
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
class AllergiesScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MedicalRecordViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var currentSheet by remember { mutableStateOf<PatientSheetType>(PatientSheetType.None) }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                if (effect is MedicalRecordEffect.ShowSnackbar) snackbarHostState.showSnackbar(effect.message)
            }
        }

        MedAIScaffold(
            title = "Allergies",
            onBackClick = { navigator.pop() },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.allergies.isEmpty()) {
                    RecordEmptyState(
                        label = "No allergies on record yet",
                        onAddClick = { currentSheet = PatientSheetType.AddAllergy }
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.allergies) { allergy ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp).padding(end = 80.dp)) {
                                        MedAIText(allergy.name, style = MedAITheme.textStyle.title.medium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        MedAIText("Symptoms: ${allergy.symptoms}", style = MedAITheme.textStyle.body.medium, color = MedAITheme.colors.text.secondary)
                                    }
                                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                        IconButton(onClick = { currentSheet = PatientSheetType.EditAllergy(allergy) }) {
                                            Icon(Icons.Default.Edit, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp))
                                        }
                                        IconButton(onClick = { viewModel.onEvent(MedicalRecordEvent.DeleteAllergy(allergy.id)) }) {
                                            Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ExtendedFloatingActionButton(
                        onClick = { currentSheet = PatientSheetType.AddAllergy },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Add Allergy") },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                        containerColor = MedAITheme.colors.primary
                    )
                }

                if (currentSheet != PatientSheetType.None) {
                    ModalBottomSheet(
                        onDismissRequest = { currentSheet = PatientSheetType.None },
                        sheetState = sheetState,
                        containerColor = MedAITheme.colors.background
                    ) {
                        PatientAllergyForm(currentSheet, onDismiss = { currentSheet = PatientSheetType.None }) { viewModel.onEvent(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientAllergyForm(sheetType: PatientSheetType, onDismiss: () -> Unit, onEvent: (MedicalRecordEvent) -> Unit) {
    val allergy = (sheetType as? PatientSheetType.EditAllergy)?.allergy
    var name by remember { mutableStateOf(allergy?.name ?: "") }
    var symptoms by remember { mutableStateOf(allergy?.symptoms ?: "") }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState())) {
        MedAIText(if (allergy == null) "Add Allergy" else "Edit Allergy", style = MedAITheme.textStyle.headline.small, modifier = Modifier.padding(bottom = 24.dp))
        MedAiTextField(value = name, onValueChange = { name = it }, placeholder = "Allergy Name")
        Spacer(modifier = Modifier.height(16.dp))
        MedAiTextArea(value = symptoms, onValueChange = { symptoms = it }, placeholder = "Symptoms / Description")
        Spacer(modifier = Modifier.height(24.dp))
        MedAIButton(text = if (allergy == null) "Add" else "Update", onClick = {
            val params = AllergyParams(name = name, symptoms = symptoms)
            if (allergy == null) onEvent(MedicalRecordEvent.AddAllergy(params))
            else onEvent(MedicalRecordEvent.EditAllergy(allergy.id, params))
            onDismiss()
        })
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- Analyses (read-only) ---
class AnalysesScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MedicalRecordViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        MedAIScaffold(title = "Analyses", onBackClick = { navigator.pop() }) {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.analyses) { analysis ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                MedAIText(analysis.type, style = MedAITheme.textStyle.title.medium)
                                MedAIText(analysis.date.toString(), style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                            }
                            Text(
                                text = analysis.status.name,
                                color = if (analysis.status == AnalysisStatus.Completed) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                style = MedAITheme.textStyle.label.medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Diseases ---
@OptIn(ExperimentalMaterial3Api::class)
class DiseasesScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MedicalRecordViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var currentSheet by remember { mutableStateOf<PatientSheetType>(PatientSheetType.None) }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { if (it is MedicalRecordEffect.ShowSnackbar) snackbarHostState.showSnackbar(it.message) }
        }

        MedAIScaffold(title = "Chronic Diseases", onBackClick = { navigator.pop() }, snackbarHost = { SnackbarHost(snackbarHostState) }) {
            val diseases = state.patientProfile?.chronicDiseases ?: emptyList()
            Box(modifier = Modifier.fillMaxSize()) {
                if (diseases.isEmpty()) {
                    RecordEmptyState(label = "No chronic diseases on record yet", onAddClick = { currentSheet = PatientSheetType.AddDisease })
                } else {
                    LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(diseases) { disease ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp).padding(end = 80.dp)) {
                                        MedAIText(disease.name, style = MedAITheme.textStyle.title.medium)
                                        MedAIText(disease.description.toString(), style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                                        MedAIText("Diagnosed: ${disease.diagnosisDate}", style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.primary)
                                    }
                                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                        IconButton(onClick = { currentSheet = PatientSheetType.EditDisease(disease) }) { Icon(Icons.Default.Edit, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp)) }
                                        IconButton(onClick = { viewModel.onEvent(MedicalRecordEvent.DeleteChronicDisease(disease.id)) }) { Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp)) }
                                    }
                                }
                            }
                        }
                    }
                    ExtendedFloatingActionButton(
                        onClick = { currentSheet = PatientSheetType.AddDisease },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Add Disease") },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                        containerColor = MedAITheme.colors.primary
                    )
                }
                if (currentSheet != PatientSheetType.None) {
                    ModalBottomSheet(onDismissRequest = { currentSheet = PatientSheetType.None }, sheetState = sheetState, containerColor = MedAITheme.colors.background) {
                        PatientDiseaseForm(currentSheet, { currentSheet = PatientSheetType.None }) { viewModel.onEvent(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientDiseaseForm(sheetType: PatientSheetType, onDismiss: () -> Unit, onEvent: (MedicalRecordEvent) -> Unit) {
    val disease = (sheetType as? PatientSheetType.EditDisease)?.disease
    var name by remember { mutableStateOf(disease?.name ?: "") }
    var desc by remember { mutableStateOf(disease?.description ?: "") }
    var date by remember { mutableStateOf(disease?.diagnosisDate ?: "") }
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState())) {
        MedAIText(if (disease == null) "Add Chronic Disease" else "Edit Chronic Disease", style = MedAITheme.textStyle.headline.small, modifier = Modifier.padding(bottom = 24.dp))
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
            val params = ChronicDiseaseParams(name = name, description = desc, diagnosisDate = date)
            if (disease == null) onEvent(MedicalRecordEvent.AddChronicDisease(params))
            else onEvent(MedicalRecordEvent.EditChronicDisease(disease.id, params))
            onDismiss()
        })
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- Surgeries ---
@OptIn(ExperimentalMaterial3Api::class)
class SurgeriesScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MedicalRecordViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var currentSheet by remember { mutableStateOf<PatientSheetType>(PatientSheetType.None) }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { if (it is MedicalRecordEffect.ShowSnackbar) snackbarHostState.showSnackbar(it.message) }
        }

        MedAIScaffold(title = "Surgeries", onBackClick = { navigator.pop() }, snackbarHost = { SnackbarHost(snackbarHostState) }) {
            val surgeries = state.patientProfile?.surgeries ?: emptyList()
            Box(modifier = Modifier.fillMaxSize()) {
                if (surgeries.isEmpty()) {
                    RecordEmptyState(label = "No surgeries on record yet", onAddClick = { currentSheet = PatientSheetType.AddSurgery })
                } else {
                    LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(surgeries) { surgery ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp).padding(end = 80.dp)) {
                                        MedAIText(surgery.name, style = MedAITheme.textStyle.title.medium)
                                        MedAIText(surgery.description.toString(), style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                                        MedAIText("Date: ${surgery.date}", style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.primary)
                                    }
                                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                        IconButton(onClick = { currentSheet = PatientSheetType.EditSurgery(surgery) }) { Icon(Icons.Default.Edit, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp)) }
                                        IconButton(onClick = { viewModel.onEvent(MedicalRecordEvent.DeleteSurgery(surgery.id)) }) { Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp)) }
                                    }
                                }
                            }
                        }
                    }
                    ExtendedFloatingActionButton(
                        onClick = { currentSheet = PatientSheetType.AddSurgery },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Add Surgery") },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                        containerColor = MedAITheme.colors.primary
                    )
                }
                if (currentSheet != PatientSheetType.None) {
                    ModalBottomSheet(onDismissRequest = { currentSheet = PatientSheetType.None }, sheetState = sheetState, containerColor = MedAITheme.colors.background) {
                        PatientSurgeryForm(currentSheet, { currentSheet = PatientSheetType.None }) { viewModel.onEvent(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientSurgeryForm(sheetType: PatientSheetType, onDismiss: () -> Unit, onEvent: (MedicalRecordEvent) -> Unit) {
    val surgery = (sheetType as? PatientSheetType.EditSurgery)?.surgery
    var name by remember { mutableStateOf(surgery?.name ?: "") }
    var desc by remember { mutableStateOf(surgery?.description ?: "") }
    var date by remember { mutableStateOf(surgery?.date ?: "") }
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState())) {
        MedAIText(if (surgery == null) "Add Surgery" else "Edit Surgery", style = MedAITheme.textStyle.headline.small, modifier = Modifier.padding(bottom = 24.dp))
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
            if (surgery == null) onEvent(MedicalRecordEvent.AddSurgery(params))
            else onEvent(MedicalRecordEvent.EditSurgery(surgery.id, params))
            onDismiss()
        })
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- Family History ---
@OptIn(ExperimentalMaterial3Api::class)
class FamilyHistoryScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MedicalRecordViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var currentSheet by remember { mutableStateOf<PatientSheetType>(PatientSheetType.None) }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { if (it is MedicalRecordEffect.ShowSnackbar) snackbarHostState.showSnackbar(it.message) }
        }

        MedAIScaffold(title = "Family History", onBackClick = { navigator.pop() }, snackbarHost = { SnackbarHost(snackbarHostState) }) {
            val histories = state.patientProfile?.familyHistories ?: emptyList()
            Box(modifier = Modifier.fillMaxSize()) {
                if (histories.isEmpty()) {
                    RecordEmptyState(label = "No family history on record yet", onAddClick = { currentSheet = PatientSheetType.AddFamily })
                } else {
                    LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(histories) { history ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp).padding(end = 80.dp)) {
                                        MedAIText("${history.relation.label}: ${history.condition}", style = MedAITheme.textStyle.title.medium)
                                        MedAIText(history.notes.toString(), style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                                    }
                                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                        IconButton(onClick = { currentSheet = PatientSheetType.EditFamily(history) }) { Icon(Icons.Default.Edit, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp)) }
                                        IconButton(onClick = { viewModel.onEvent(MedicalRecordEvent.DeleteFamilyHistory(history.id)) }) { Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp)) }
                                    }
                                }
                            }
                        }
                    }
                    ExtendedFloatingActionButton(
                        onClick = { currentSheet = PatientSheetType.AddFamily },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Add Family History") },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                        containerColor = MedAITheme.colors.primary
                    )
                }
                if (currentSheet != PatientSheetType.None) {
                    ModalBottomSheet(onDismissRequest = { currentSheet = PatientSheetType.None }, sheetState = sheetState, containerColor = MedAITheme.colors.background) {
                        PatientFamilyForm(currentSheet, { currentSheet = PatientSheetType.None }) { viewModel.onEvent(it) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientFamilyForm(sheetType: PatientSheetType, onDismiss: () -> Unit, onEvent: (MedicalRecordEvent) -> Unit) {
    val history = (sheetType as? PatientSheetType.EditFamily)?.history
    var relation by remember { mutableStateOf(history?.relation ?: FamilyRelation.Father) }
    var condition by remember { mutableStateOf(history?.condition ?: "") }
    var notes by remember { mutableStateOf(history?.notes ?: "") }
    var relationExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState())) {
        MedAIText(if (history == null) "Add Family History" else "Edit Family History", style = MedAITheme.textStyle.headline.small, modifier = Modifier.padding(bottom = 24.dp))

        MedAIText("Relation", style = MedAITheme.textStyle.label.medium, modifier = Modifier.padding(bottom = 8.dp))
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
                    DropdownMenuItem(
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
            val params = FamilyHistoryParams(relation = relation, condition = condition, notes = notes)
            if (history == null) onEvent(MedicalRecordEvent.AddFamilyHistory(params))
            else onEvent(MedicalRecordEvent.EditFamilyHistory(history.id, params))
            onDismiss()
        })
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- Emergency Contacts ---
@OptIn(ExperimentalMaterial3Api::class)
class EmergencyContactsScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MedicalRecordViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var currentSheet by remember { mutableStateOf<PatientSheetType>(PatientSheetType.None) }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { if (it is MedicalRecordEffect.ShowSnackbar) snackbarHostState.showSnackbar(it.message) }
        }

        MedAIScaffold(title = "Emergency Contacts", onBackClick = { navigator.pop() }, snackbarHost = { SnackbarHost(snackbarHostState) }) {
            val contacts = state.patientProfile?.emergencyContacts ?: emptyList()
            Box(modifier = Modifier.fillMaxSize()) {
                if (contacts.isEmpty()) {
                    RecordEmptyState(label = "No emergency contacts on record yet", onAddClick = { currentSheet = PatientSheetType.AddEmergency })
                } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(contacts) { contact ->
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)) {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(16.dp).padding(end = 80.dp)) {
                                            MedAIText(contact.name, style = MedAITheme.textStyle.title.medium)
                                            MedAIText("${contact.relation} ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â¢ ${contact.phoneNumber}", style = MedAITheme.textStyle.body.small)
                                            MedAIText(contact.address, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                                        }
                                        Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                            IconButton(onClick = { currentSheet = PatientSheetType.EditEmergency(contact) }) { Icon(Icons.Default.Edit, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp)) }
                                            IconButton(onClick = { viewModel.onEvent(MedicalRecordEvent.DeleteEmergencyContact(contact.id)) }) { Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp)) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ExtendedFloatingActionButton(
                        onClick = { currentSheet = PatientSheetType.AddEmergency },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Add Contact") },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                        containerColor = MedAITheme.colors.primary
                    )
                }
                if (currentSheet != PatientSheetType.None) {
                    ModalBottomSheet(onDismissRequest = { currentSheet = PatientSheetType.None }, sheetState = sheetState, containerColor = MedAITheme.colors.background) {
                        PatientEmergencyForm(currentSheet, { currentSheet = PatientSheetType.None }) { viewModel.onEvent(it) }
                    }
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientEmergencyForm(sheetType: PatientSheetType, onDismiss: () -> Unit, onEvent: (MedicalRecordEvent) -> Unit) {
    val contact = (sheetType as? PatientSheetType.EditEmergency)?.contact
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
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState())) {
        MedAIText(if (contact == null) "Add Emergency Contact" else "Edit Emergency Contact", style = MedAITheme.textStyle.headline.small, modifier = Modifier.padding(bottom = 24.dp))
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
                    DropdownMenuItem(
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
                name = name, relation = relation.name.lowercase(), phoneNumber = phone,
                email = email, address = address, notes = notes
            )
            if (contact == null) onEvent(MedicalRecordEvent.AddEmergencyContact(params))
            else onEvent(MedicalRecordEvent.EditEmergencyContact(contact.id, params))
            onDismiss()
        })
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- Vaccinations (read-only) ---
class VaccinationsScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MedicalRecordViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        MedAIScaffold(title = "Vaccinations", onBackClick = { navigator.pop() }) {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.vaccinations) { vaccine ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                MedAIText(vaccine.name, style = MedAITheme.textStyle.title.medium)
                                MedAIText(vaccine.status.name, color = MedAITheme.colors.primary, style = MedAITheme.textStyle.label.medium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            MedAIText("Taken: ${vaccine.dateAdministered}", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                            vaccine.nextDoseDate?.let {
                                MedAIText("Next Dose: $it", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Medical History (read-only) ---
class MedicalHistoryScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MedicalRecordViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        MedAIScaffold(title = "Medical History", onBackClick = { navigator.pop() }) {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.medicalHistory) { history ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            MedAIText(history.condition, style = MedAITheme.textStyle.title.medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            MedAIText("Status: ${history.status}", style = MedAITheme.textStyle.body.medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            MedAIText("Treatment: ${history.treatmentPlan}", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            MedAIText("Doctor: ${history.attendingDoctor}", style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.primary)
                        }
                    }
                }
            }
        }
    }
}
