package org.example.project.presentation.shared.records.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.patient.*

@Composable
fun AllergyList(
    allergies: List<AllergyEntity>,
    onEdit: (AllergyEntity) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(allergies) { allergy ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp).padding(end = 80.dp)) {
                        MedAIText(allergy.name, style = MedAITheme.textStyle.title.medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        MedAIText("Symptoms: ", style = MedAITheme.textStyle.body.medium, color = MedAITheme.colors.text.secondary)
                    }
                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                        IconButton(onClick = { onEdit(allergy) }) {
                            Icon(Icons.Default.Edit, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = { onDelete(allergy.id) }) {
                            Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiseaseList(
    diseases: List<ChronicDiseaseEntity>,
    onEdit: (ChronicDiseaseEntity) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(diseases) { disease ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp).padding(end = 80.dp)) {
                        MedAIText(disease.name, style = MedAITheme.textStyle.title.medium)
                        MedAIText(disease.description.toString(), style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                        MedAIText("Diagnosed: ", style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.primary)
                    }
                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                        IconButton(onClick = { onEdit(disease) }) { Icon(Icons.Default.Edit, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp)) }
                        IconButton(onClick = { onDelete(disease.id) }) { Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun SurgeryList(
    surgeries: List<SurgeryEntity>,
    onEdit: (SurgeryEntity) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(surgeries) { surgery ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp).padding(end = 80.dp)) {
                        MedAIText(surgery.name, style = MedAITheme.textStyle.title.medium)
                        MedAIText(surgery.description.toString(), style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                        MedAIText("Date: ", style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.primary)
                    }
                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                        IconButton(onClick = { onEdit(surgery) }) { Icon(Icons.Default.Edit, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp)) }
                        IconButton(onClick = { onDelete(surgery.id) }) { Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyHistoryList(
    histories: List<FamilyHistoryEntity>,
    onEdit: (FamilyHistoryEntity) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(histories) { history ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp).padding(end = 80.dp)) {
                        MedAIText(": ", style = MedAITheme.textStyle.title.medium)
                        MedAIText(history.notes.toString(), style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                    }
                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                        IconButton(onClick = { onEdit(history) }) { Icon(Icons.Default.Edit, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp)) }
                        IconButton(onClick = { onDelete(history.id) }) { Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyContactList(
    contacts: List<EmergencyContactEntity>,
    onEdit: (EmergencyContactEntity) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(contacts) { contact ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp).padding(end = 80.dp)) {
                        MedAIText(contact.name, style = MedAITheme.textStyle.title.medium)
                        MedAIText(" | ", style = MedAITheme.textStyle.body.medium, color = MedAITheme.colors.text.secondary)
                    }
                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                        IconButton(onClick = { onEdit(contact) }) { Icon(Icons.Default.Edit, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(20.dp)) }
                        IconButton(onClick = { onDelete(contact.id) }) { Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.secondary, modifier = Modifier.size(20.dp)) }
                    }
                }
            }
        }
    }
}
