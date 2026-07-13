package org.example.project.presentation.appointmentScreen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.core.presentation.util.toUiString
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.appointment.AppointmentDetail

@Composable
fun UnifiedAppointmentHeaderCard(
    appointment: AppointmentDetail,
    onViewPatientRecords: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Clean Doctor Name from trailing ID (e.g. "Doctor #4" -> "Doctor")
    val cleanedDoctorName = remember(appointment.doctorName) {
        appointment.doctorName.replace(Regex("#\\d+"), "").trim()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 600)) +
                slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(durationMillis = 600)
                ),
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(MedAITheme.dimensions.radiusExtraLarge),
            colors = CardDefaults.cardColors(
                containerColor = MedAITheme.colors.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = MedAITheme.dimensions.elevationSmall
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MedAITheme.colors.neutral.copy(alpha = 0.08f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Participants Section (Patient stacked below Doctor)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MedAITheme.dimensions.large)
                ) {
                    // Doctor Info (Top)
                    ParticipantIndicator(
                        name = cleanedDoctorName,
                        role = appointment.specialty,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

                    // Patient Info (Below Doctor - Clickable)
                    ClickablePatientSnippet(
                        name = appointment.patientName,
                        onClick = { onViewPatientRecords(appointment.patientId) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Divider separating content and date/time band
                HorizontalDivider(
                    color = MedAITheme.colors.neutral.copy(alpha = 0.08f),
                    thickness = 1.dp
                )

                // Date & Time Band across the bottom
                AppointmentTimeBand(
                    dateTimeText = appointment.date.toUiString()
                )
            }
        }
    }
}

@Composable
fun ParticipantIndicator(
    name: String,
    role: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Grey Placeholder Avatar
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(MedAITheme.dimensions.spacing48)
                .clip(CircleShape)
                .background(MedAITheme.colors.neutral.copy(alpha = 0.12f))
        ) {
            MedAIText(
                text = name.firstOrNull()?.toString()?.uppercase() ?: "?",
                style = MedAITheme.textStyle.title.medium,
                color = MedAITheme.colors.text.secondary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(MedAITheme.dimensions.small))

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            MedAIText(
                text = name,
                style = MedAITheme.textStyle.body.medium,
                color = MedAITheme.colors.text.primary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            MedAIText(
                text = role,
                style = MedAITheme.textStyle.body.small,
                color = MedAITheme.colors.text.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ClickablePatientSnippet(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusLarge))
            .background(MedAITheme.colors.primary.copy(alpha = 0.06f))
            .clickable { onClick() }
            .background(Color.Transparent)
            .padding(MedAITheme.dimensions.small)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Patient Placeholder Avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(MedAITheme.dimensions.spacing48 - 8.dp) // slightly smaller for layout distinction
                        .clip(CircleShape)
                        .background(MedAITheme.colors.primary.copy(alpha = 0.15f))
                ) {
                    MedAIText(
                        text = name.firstOrNull()?.toString()?.uppercase() ?: "?",
                        style = MedAITheme.textStyle.title.small,
                        color = MedAITheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(MedAITheme.dimensions.small))

                Column {
                    MedAIText(
                        text = name,
                        style = MedAITheme.textStyle.body.medium,
                        color = MedAITheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    MedAIText(
                        text = "Patient Records",
                        style = MedAITheme.textStyle.label.small,
                        color = MedAITheme.colors.primary.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View Patient Records",
                tint = MedAITheme.colors.primary,
                modifier = Modifier
                    .size(MedAITheme.dimensions.iconMedium)
                    .padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun AppointmentTimeBand(
    dateTimeText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MedAITheme.colors.primary.copy(alpha = 0.03f))
            .padding(
                horizontal = MedAITheme.dimensions.large,
                vertical = MedAITheme.dimensions.medium
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MedAITheme.colors.primary,
                modifier = Modifier.size(MedAITheme.dimensions.iconSmall)
            )

            Spacer(modifier = Modifier.width(MedAITheme.dimensions.extraSmall))

            // Split date and time if it contains |
            val parts = dateTimeText.split("|")
            if (parts.size == 2) {
                MedAIText(
                    text = parts[0].trim(),
                    style = MedAITheme.textStyle.label.large,
                    color = MedAITheme.colors.text.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(MedAITheme.dimensions.small))

                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MedAITheme.colors.primary,
                    modifier = Modifier.size(MedAITheme.dimensions.iconSmall)
                )

                Spacer(modifier = Modifier.width(MedAITheme.dimensions.extraSmall))

                MedAIText(
                    text = parts[1].trim(),
                    style = MedAITheme.textStyle.label.large,
                    color = MedAITheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                MedAIText(
                    text = dateTimeText,
                    style = MedAITheme.textStyle.label.large,
                    color = MedAITheme.colors.text.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
