package org.example.project.presentation.appointmentScreen.voiceReport.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.voice_report.VoiceReport

@Composable
fun ClinicalReportCard(
    report: VoiceReport,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    var isTranscriptionExpanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MedAITheme.dimensions.radiusLarge),
        colors = CardDefaults.cardColors(
            containerColor = MedAITheme.colors.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = MedAITheme.dimensions.elevationSmall
        )
    ) {
        Column(modifier = Modifier.padding(MedAITheme.dimensions.large)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Report Info",
                        tint = MedAITheme.colors.accent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(MedAITheme.dimensions.small))
                    MedAIText(
                        text = "AI Clinical Report",
                        style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold),
                        color = MedAITheme.colors.text.primary
                    )
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Expand/Collapse",
                    tint = MedAITheme.colors.text.secondary,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationState)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = MedAITheme.dimensions.large)) {
                    val clinical = report.clinicalReport

                    if (clinical != null) {
                        // Systematic Summary
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(MedAITheme.dimensions.radiusMedium))
                                .background(MedAITheme.colors.primary.copy(alpha = 0.08f))
                                .border(
                                    width = 1.dp,
                                    color = MedAITheme.colors.primary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(MedAITheme.dimensions.radiusMedium)
                                )
                                .padding(MedAITheme.dimensions.large)
                        ) {
                            Column {
                                MedAIText(
                                    text = "Systemic Summary",
                                    style = MedAITheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                                    color = MedAITheme.colors.primary
                                )
                                Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraSmall))
                                MedAIText(
                                    text = clinical.systematicSummary,
                                    style = MedAITheme.textStyle.body.medium,
                                    color = MedAITheme.colors.text.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

                        // Symptoms Table Title
                        MedAIText(
                            text = "Extracted Symptoms",
                            style = MedAITheme.textStyle.title.small.copy(fontWeight = FontWeight.Bold),
                            color = MedAITheme.colors.text.primary,
                            modifier = Modifier.padding(bottom = MedAITheme.dimensions.small)
                        )

                        if (clinical.symptoms.isEmpty()) {
                            MedAIText(
                                text = "No symptoms extracted.",
                                style = MedAITheme.textStyle.body.medium,
                                color = MedAITheme.colors.text.tertiary,
                                modifier = Modifier.padding(vertical = MedAITheme.dimensions.small)
                            )
                        } else {
                            // Custom Symptoms Table
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(MedAITheme.dimensions.radiusMedium))
                                    .border(
                                        width = 1.dp,
                                        color = MedAITheme.colors.neutral.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(MedAITheme.dimensions.radiusMedium)
                                    )
                            ) {
                                // Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MedAITheme.colors.neutral.copy(alpha = 0.1f))
                                        .padding(horizontal = MedAITheme.dimensions.medium, vertical = MedAITheme.dimensions.small),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    MedAIText(
                                        text = "Arabic slang phrase",
                                        style = MedAITheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                                        color = MedAITheme.colors.text.secondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    MedAIText(
                                        text = "Clinical Term",
                                        style = MedAITheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                                        color = MedAITheme.colors.text.secondary,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End
                                    )
                                }

                                // Rows
                                clinical.symptoms.forEachIndexed { index, symptom ->
                                    val rowBg = if (index % 2 == 0) Color.Transparent else MedAITheme.colors.neutral.copy(alpha = 0.03f)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(rowBg)
                                            .padding(horizontal = MedAITheme.dimensions.medium, vertical = MedAITheme.dimensions.medium),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Arabic text aligned correctly (RTL behavior)
                                        MedAIText(
                                            text = symptom.descriptionAr,
                                            style = MedAITheme.textStyle.body.medium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MedAITheme.colors.text.primary,
                                            modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.Start
                                        )
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .wrapContentWidth(Alignment.End)
                                                .clip(RoundedCornerShape(MedAITheme.dimensions.radiusSmall))
                                                .background(MedAITheme.colors.accent.copy(alpha = 0.12f))
                                                .padding(horizontal = MedAITheme.dimensions.small, vertical = 2.dp)
                                        ) {
                                            MedAIText(
                                                text = symptom.clinicalTerm,
                                                style = MedAITheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                                                color = MedAITheme.colors.accent
                                            )
                                        }
                                    }

                                    if (index < clinical.symptoms.lastIndex) {
                                        Divider(color = MedAITheme.colors.neutral.copy(alpha = 0.1f))
                                    }
                                }
                            }
                        }
                    } else if (report.errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(MedAITheme.dimensions.radiusMedium))
                                .background(MedAITheme.colors.status.errorContainer)
                                .padding(MedAITheme.dimensions.medium)
                        ) {
                            MedAIText(
                                text = "Error: ${report.errorMessage}",
                                style = MedAITheme.textStyle.body.medium,
                                color = MedAITheme.colors.status.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

                    // Transcription Section
                    if (!report.transcription.isNullOrBlank()) {
                        Divider(color = MedAITheme.colors.neutral.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isTranscriptionExpanded = !isTranscriptionExpanded }
                                .padding(vertical = MedAITheme.dimensions.small),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = "Transcript icon",
                                    tint = MedAITheme.colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(MedAITheme.dimensions.small))
                                MedAIText(
                                    text = "Full Transcription",
                                    style = MedAITheme.textStyle.title.small.copy(fontWeight = FontWeight.Bold),
                                    color = MedAITheme.colors.text.primary
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Expand transcript",
                                tint = MedAITheme.colors.text.secondary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(if (isTranscriptionExpanded) 180f else 0f)
                            )
                        }

                        AnimatedVisibility(visible = isTranscriptionExpanded) {
                            Column(modifier = Modifier.padding(top = MedAITheme.dimensions.small)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(MedAITheme.dimensions.radiusMedium))
                                        .background(MedAITheme.colors.neutral.copy(alpha = 0.05f))
                                        .padding(MedAITheme.dimensions.medium)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(report.transcription))
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copy to clipboard",
                                                    tint = MedAITheme.colors.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        // Transcript might contain Arabic mixed with slang, align nicely
                                        MedAIText(
                                            text = report.transcription,
                                            style = MedAITheme.textStyle.body.medium,
                                            color = MedAITheme.colors.text.secondary,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
