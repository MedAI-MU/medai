package org.example.project.presentation.secretary.doctors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.design_system.theme.MedAITheme
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.domain.model.doctor.Doctor
import org.example.project.domain.model.doctor.Speciality
import org.example.project.presentation.schedule.ScheduleScreen

class SecretaryDoctorListScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<SecretaryDoctorListViewModel>()
        val state by viewModel.state.collectAsState()
        val dimensions = LocalDimensions.current

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is SecretaryDoctorListEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

        MedAIScaffold(
            topBar = {
                MedAiAppBar(
                    title = "Doctors",
                    centerTitle = false,
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(SecretaryDoctorListEvent.ShowCreateSpecialityDialog) }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Speciality")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Search bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(SecretaryDoctorListEvent.OnSearchQueryChanged(it)) },
                    placeholder = { Text("Search doctors...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onEvent(SecretaryDoctorListEvent.OnSearchQueryChanged("")) }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensions.medium, vertical = dimensions.small),
                    shape = RoundedCornerShape(dimensions.radiusMedium),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedAITheme.colors.primary,
                        unfocusedBorderColor = MedAITheme.colors.neutral.copy(alpha = 0.3f)
                    )
                )

                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MedAITheme.colors.primary)
                        }
                    }
                    state.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = state.error ?: "Unknown error",
                                    color = MedAITheme.colors.status.error,
                                    style = MedAITheme.textStyle.body.medium
                                )
                                Spacer(modifier = Modifier.height(dimensions.medium))
                                Button(onClick = { viewModel.onEvent(SecretaryDoctorListEvent.LoadDoctors) }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    state.filteredDoctors.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.PersonSearch,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MedAITheme.colors.text.tertiary
                                )
                                Spacer(modifier = Modifier.height(dimensions.medium))
                                Text(
                                    text = "No doctors found",
                                    style = MedAITheme.textStyle.title.medium,
                                    color = MedAITheme.colors.text.secondary
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = dimensions.medium, vertical = dimensions.small),
                            verticalArrangement = Arrangement.spacedBy(dimensions.small)
                        ) {
                            items(state.filteredDoctors) { doctor ->
                                DoctorCard(
                                    doctor = doctor,
                                    onScheduleClick = {
                                        navigator.push(ScheduleScreen(doctorId = doctor.id.toIntOrNull() ?: 0))
                                    },
                                    onSpecialitiesClick = {
                                        viewModel.onEvent(SecretaryDoctorListEvent.ShowSpecialityDialog(doctor))
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(dimensions.medium)) }
                        }
                    }
                }
            }

            // Loading overlay for speciality actions
            if (state.isActionLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MedAITheme.colors.primary)
                }
            }
        }

        // Speciality management dialog
        if (state.showSpecialityDialog && state.selectedDoctorForSpeciality != null) {
            SpecialityManagementDialog(
                doctor = state.selectedDoctorForSpeciality!!,
                allSpecialities = state.allSpecialities,
                onDismiss = { viewModel.onEvent(SecretaryDoctorListEvent.DismissSpecialityDialog) },
                onCreateSpecialityClick = { viewModel.onEvent(SecretaryDoctorListEvent.ShowCreateSpecialityDialog) },
                onAssign = { specialityId, isPrimary, years ->
                    viewModel.onEvent(SecretaryDoctorListEvent.AssignSpeciality(
                        doctorId = state.selectedDoctorForSpeciality!!.id,
                        specialityId = specialityId,
                        isPrimary = isPrimary,
                        yearsOfExperience = years
                    ))
                }
            )
        }

        // Create Speciality Dialog
        if (state.showCreateSpecialityDialog) {
            var name by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(SecretaryDoctorListEvent.DismissCreateSpecialityDialog) },
                title = { Text("Create New Speciality") },
                text = {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Speciality Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(dimensions.radiusMedium)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onEvent(SecretaryDoctorListEvent.CreateSpeciality(name)) },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onEvent(SecretaryDoctorListEvent.DismissCreateSpecialityDialog) }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun DoctorCard(
    doctor: Doctor,
    onScheduleClick: () -> Unit,
    onSpecialitiesClick: () -> Unit
) {
    val dimensions = LocalDimensions.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(dimensions.medium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MedAITheme.colors.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MedAITheme.colors.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.small))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doctor.name,
                        style = MedAITheme.textStyle.title.medium,
                        fontWeight = FontWeight.Bold,
                        color = MedAITheme.colors.text.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = doctor.specialty,
                        style = MedAITheme.textStyle.body.small,
                        color = MedAITheme.colors.text.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.small))
            HorizontalDivider(color = MedAITheme.colors.neutral.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(dimensions.extraSmall))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onScheduleClick) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Schedule", style = MedAITheme.textStyle.label.medium)
                }
                TextButton(onClick = onSpecialitiesClick) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Specialities", style = MedAITheme.textStyle.label.medium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpecialityManagementDialog(
    doctor: Doctor,
    allSpecialities: List<Speciality>,
    onDismiss: () -> Unit,
    onCreateSpecialityClick: () -> Unit,
    onAssign: (specialityId: Int, isPrimary: Boolean, yearsOfExperience: Int) -> Unit
) {
    var selectedSpecialityId by remember { mutableStateOf(allSpecialities.firstOrNull()?.id ?: 0) }
    var isPrimary by remember { mutableStateOf(false) }
    var yearsOfExperience by remember { mutableStateOf("0") }
    var showDropdown by remember { mutableStateOf(false) }
    val dimensions = LocalDimensions.current

    var prevSize by remember { mutableStateOf(allSpecialities.size) }
    LaunchedEffect(allSpecialities.size) {
        if (allSpecialities.size > prevSize) {
            selectedSpecialityId = allSpecialities.lastOrNull()?.id ?: selectedSpecialityId
        }
        prevSize = allSpecialities.size
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Manage Specialities for ${doctor.name}")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimensions.small)
            ) {
                Text(
                    "Current: ${doctor.specialty}",
                    style = MedAITheme.textStyle.body.medium,
                    color = MedAITheme.colors.text.secondary
                )

                HorizontalDivider()

                Text(
                    "Assign New Speciality",
                    style = MedAITheme.textStyle.label.medium,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.text.primary
                )

                // Speciality dropdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensions.small)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = showDropdown,
                        onExpandedChange = { showDropdown = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = allSpecialities.find { it.id == selectedSpecialityId }?.name ?: "Select...",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(dimensions.radiusMedium),
                            label = { Text("Speciality") }
                        )
                        ExposedDropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            allSpecialities.forEach { speciality ->
                                DropdownMenuItem(
                                    text = { Text(speciality.name) },
                                    onClick = {
                                        selectedSpecialityId = speciality.id
                                        showDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onCreateSpecialityClick,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .background(MedAITheme.colors.primary.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Speciality",
                            tint = MedAITheme.colors.primary
                        )
                    }
                }

                // Years of experience
                OutlinedTextField(
                    value = yearsOfExperience,
                    onValueChange = { yearsOfExperience = it.filter { c -> c.isDigit() } },
                    label = { Text("Years of Experience") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(dimensions.radiusMedium)
                )

                // Is primary checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPrimary,
                        onCheckedChange = { isPrimary = it }
                    )
                    Text(
                        "Set as primary speciality",
                        style = MedAITheme.textStyle.body.small,
                        color = MedAITheme.colors.text.primary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAssign(
                        selectedSpecialityId,
                        isPrimary,
                        yearsOfExperience.toIntOrNull() ?: 0
                    )
                },
                enabled = selectedSpecialityId > 0,
                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
            ) {
                Text("Assign")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
