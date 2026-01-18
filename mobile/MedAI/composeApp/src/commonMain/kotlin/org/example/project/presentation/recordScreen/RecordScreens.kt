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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.AnalysisStatus
import org.example.project.domain.model.PatientEntity

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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navigator.push(AddRecordScreen()) },
                    colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Records")
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. Dashboard Screen (Main Entry)
// -----------------------------------------------------------------------------
class RecordsDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<MedicalRecordViewModel>()
        val state by viewModel.state.collectAsState()

        MedAIScaffold(title = "Medical Records") {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MedAITheme.colors.primary)
                }
            } else if (state.patientProfile == null) {
                // If no profile data, redirect or show empty state
                // (In a real app, you might check a specific flag "hasRecords")
                // For this mock logic, we assume if profile is null, it's empty.
                // But since mock repo always returns a profile, this won't trigger unless we empty the mock.
                // navigator.replace(RecordsEmptyScreen()) // Logic depends on requirements

                // Just render Dashboard assuming data exists as per Mock
                DashboardContent(state, navigator)
            } else {
                DashboardContent(state, navigator)
            }
        }
    }

    @Composable
    fun DashboardContent(state: MedicalRecordState, navigator: cafe.adriel.voyager.navigator.Navigator) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // User Header
            state.patientProfile?.let { UserHeader(it) }

            Spacer(modifier = Modifier.height(24.dp))

            // Grid Menu
            val items = listOf(
                DashboardItem("Allergies", Icons.Default.LocalHospital, Color(0xFFFFCDD2)) { navigator.push(AllergiesScreen()) },
                DashboardItem("Analysis", Icons.Default.Science, Color(0xFFE1BEE7)) { navigator.push(AnalysesScreen()) },
                DashboardItem("Vaccinations", Icons.Default.Vaccines, Color(0xFFC8E6C9)) { navigator.push(VaccinationsScreen()) },
                DashboardItem("History", Icons.Default.History, Color(0xFFBBDEFB)) { navigator.push(MedicalHistoryScreen()) }
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { item ->
                    DashboardCard(item)
                }
            }
        }
    }
}

data class DashboardItem(val title: String, val icon: ImageVector, val color: Color, val onClick: () -> Unit)

@Composable
fun DashboardCard(item: DashboardItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { item.onClick() },
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
fun UserHeader(profile: PatientEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MedAITheme.colors.primary.copy(alpha = 0.1f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            MedAIText(profile.fullName, style = MedAITheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(4.dp))
            MedAIText("Blood Type: ${profile.bloodType.label}", style = MedAITheme.textStyle.body.medium)
            MedAIText("Height: ${profile.height} cm", style = MedAITheme.textStyle.body.medium)
            MedAIText("Weight: ${profile.weight} kg", style = MedAITheme.textStyle.body.medium)
        }
    }
}

// -----------------------------------------------------------------------------
// 3. Sub-Screens (Lists)
// -----------------------------------------------------------------------------

// --- Allergies ---
class AllergiesScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MedicalRecordViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        MedAIScaffold(title = "Allergies", onBackClick = { navigator.pop() }) {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.allergies) { allergy ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            MedAIText(allergy.name, style = MedAITheme.textStyle.title.medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            MedAIText("Symptoms: ${allergy.symptoms}", style = MedAITheme.textStyle.body.medium, color = MedAITheme.colors.text.secondary)
                        }
                    }
                }
            }
        }
    }
}

// --- Analyses ---
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
                            // Status Chip logic could go here
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

// --- Vaccinations ---
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

// --- Medical History ---
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

// 4. Add Record Screen (Placeholder)
class AddRecordScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        MedAIScaffold(title = "Add Record", onBackClick = { navigator.pop() }) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Form to add profile data goes here")
            }
        }
    }
}
