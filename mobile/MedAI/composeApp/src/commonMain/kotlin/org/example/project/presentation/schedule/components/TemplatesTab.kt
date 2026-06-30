package org.example.project.presentation.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import cafe.adriel.voyager.koin.getScreenModel
import org.example.project.design_system.theme.MedAITheme
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.domain.model.schedule.*
import org.koin.core.parameter.parametersOf


// ==================== Templates Tab ====================

@Composable
fun TemplatesTab(
    state: ScheduleUiState<PagedTemplates>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onEdit: (ScheduleTemplate) -> Unit,
    onDelete: (Int) -> Unit,
    onApply: (ScheduleTemplate) -> Unit,
    onRetry: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Search templates...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        onSearchQueryChanged("")
                        onSearch()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MedAITheme.colors.primary,
                unfocusedBorderColor = MedAITheme.colors.neutral.copy(alpha = 0.3f)
            )
        )

        when (state) {
            is ScheduleUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MedAITheme.colors.primary)
                }
            }
            is ScheduleUiState.Error -> {
                ErrorContent(message = state.message, onRetry = { onRetry(1) })
            }
            is ScheduleUiState.Success -> {
                if (state.data.data.isEmpty()) {
                    EmptyContent(
                        icon = Icons.Default.ContentCopy,
                        message = "No templates found",
                        subtitle = "Create your first schedule template using the + button"
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data.data) { template ->
                            TemplateCard(
                                template = template,
                                onEdit = { onEdit(template) },
                                onDelete = { onDelete(template.id) },
                                onApply = { onApply(template) }
                            )
                        }
                        // Bottom spacing for FAB
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateCard(
    template: ScheduleTemplate,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onApply: () -> Unit
) {
    val weekDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.name,
                        style = MedAITheme.textStyle.title.medium,
                        fontWeight = FontWeight.Bold,
                        color = MedAITheme.colors.text.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${template.slots.size} slots · Created by ${template.createdByName}",
                        style = MedAITheme.textStyle.body.small,
                        color = MedAITheme.colors.text.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Week day pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                weekDays.forEachIndexed { index, dayName ->
                    val isActive = template.slots.any { it.weekDay == index }
                    val bgColor by animateColorAsState(
                        if (isActive) MedAITheme.colors.primary else Color.Transparent
                    )
                    val textColor by animateColorAsState(
                        if (isActive) MedAITheme.colors.onPrimary else MedAITheme.colors.text.tertiary
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .border(
                                width = 1.dp,
                                color = if (isActive) Color.Transparent else MedAITheme.colors.neutral.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayName.first().toString(),
                            style = MedAITheme.textStyle.label.small,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time slots preview
            val groupedByDay = template.slots.groupBy { it.weekDay }
                .entries.sortedBy { it.key }
                .take(3) // Show first 3 days

            groupedByDay.forEach { (day, slots) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = weekDays.getOrElse(day) { "?" },
                        style = MedAITheme.textStyle.label.medium,
                        fontWeight = FontWeight.SemiBold,
                        color = MedAITheme.colors.primary,
                        modifier = Modifier.width(40.dp)
                    )
                    Text(
                        text = slots.joinToString(" · ") { "${it.startTime}-${it.endTime}" },
                        style = MedAITheme.textStyle.body.small,
                        color = MedAITheme.colors.text.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (groupedByDay.size < template.slots.groupBy { it.weekDay }.size) {
                Text(
                    text = "+${template.slots.groupBy { it.weekDay }.size - groupedByDay.size} more days",
                    style = MedAITheme.textStyle.label.small,
                    color = MedAITheme.colors.text.tertiary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MedAITheme.colors.neutral.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onApply) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply", style = MedAITheme.textStyle.label.medium)
                }
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", style = MedAITheme.textStyle.label.medium)
                }
                TextButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MedAITheme.colors.status.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", style = MedAITheme.textStyle.label.medium)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Template") },
            text = { Text("Are you sure you want to delete \"${template.name}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false; onDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MedAITheme.colors.status.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
