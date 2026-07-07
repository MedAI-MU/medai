package org.example.project.presentation.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.schedule.*

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedAITheme.colors.background)
    ) {
        // Search Bar container with subtle elevation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, MedAITheme.colors.neutral.copy(alpha = 0.05f))
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = {
                    Text(
                        "Search templates...",
                        style = MedAITheme.textStyle.body.medium,
                        color = MedAITheme.colors.text.tertiary
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MedAITheme.colors.text.secondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            onSearchQueryChanged("")
                            onSearch()
                        }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MedAITheme.colors.text.secondary
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                textStyle = MedAITheme.textStyle.body.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedAITheme.colors.primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MedAITheme.colors.neutral.copy(alpha = 0.03f),
                    unfocusedContainerColor = MedAITheme.colors.neutral.copy(alpha = 0.03f),
                )
            )
        }

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
                        icon = Icons.Outlined.ContentCopy,
                        message = "No templates found",
                        subtitle = "Create your first schedule template using the '+' button below"
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        item { Spacer(modifier = Modifier.height(100.dp)) }
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
        border = BorderStroke(1.dp, MedAITheme.colors.neutral.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.name,
                        style = MedAITheme.textStyle.headline.small,
                        fontWeight = FontWeight.Bold,
                        color = MedAITheme.colors.text.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${template.slots.size} recurring slots · By ${template.createdByName}",
                        style = MedAITheme.textStyle.body.small,
                        color = MedAITheme.colors.text.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Week Day Circular Indicators
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                weekDays.forEachIndexed { index, dayName ->
                    val isActive = template.slots.any { it.weekDay == index }
                    val bgColor by animateColorAsState(
                        if (isActive) MedAITheme.colors.primary else MedAITheme.colors.neutral.copy(alpha = 0.05f)
                    )
                    val textColor by animateColorAsState(
                        if (isActive) MedAITheme.colors.onPrimary else MedAITheme.colors.text.tertiary
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(horizontal = 3.dp)
                            .clip(CircleShape)
                            .background(bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayName.take(1),
                            style = MedAITheme.textStyle.label.small,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Day Slots Previews Section
            val groupedByDay = template.slots.groupBy { it.weekDay }
                .entries.sortedBy { it.key }
                .take(3) // Show first 3 active days

            if (groupedByDay.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.neutral.copy(alpha = 0.03f)),
                    border = BorderStroke(1.dp, MedAITheme.colors.neutral.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        groupedByDay.forEach { (day, slots) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(MedAITheme.colors.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = weekDays.getOrElse(day) { "?" },
                                        style = MedAITheme.textStyle.label.small,
                                        fontWeight = FontWeight.Bold,
                                        color = MedAITheme.colors.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = slots.joinToString(" , ") { "${it.startTime.take(5)} - ${it.endTime.take(5)}" },
                                    style = MedAITheme.textStyle.body.small,
                                    fontWeight = FontWeight.Medium,
                                    color = MedAITheme.colors.text.secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        val remainingDays = template.slots.groupBy { it.weekDay }.size - groupedByDay.size
                        if (remainingDays > 0) {
                            Text(
                                text = "+$remainingDays more day${if (remainingDays > 1) "s" else ""}",
                                style = MedAITheme.textStyle.label.small,
                                color = MedAITheme.colors.text.tertiary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MedAITheme.colors.neutral.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete text button on the far left
                TextButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MedAITheme.colors.status.error)
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", style = MedAITheme.textStyle.label.medium, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Edit outlined button
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MedAITheme.colors.primary.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MedAITheme.colors.primary)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit", style = MedAITheme.textStyle.label.medium, fontWeight = FontWeight.Bold)
                    }

                    // Apply filled button
                    Button(
                        onClick = onApply,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
                    ) {
                        Icon(Icons.Outlined.PlayCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply", style = MedAITheme.textStyle.label.medium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = {
                Text(
                    "Delete Template",
                    style = MedAITheme.textStyle.headline.small,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete \"${template.name}\"? This will permanently remove this recurring rule. This action cannot be undone.",
                    style = MedAITheme.textStyle.body.medium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false; onDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MedAITheme.colors.status.error)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = MedAITheme.colors.text.secondary)
                }
            }
        )
    }
}
