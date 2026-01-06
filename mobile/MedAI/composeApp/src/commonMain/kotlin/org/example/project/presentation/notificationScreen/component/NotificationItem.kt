package org.example.project.presentation.notificationScreen.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.Notification
import org.example.project.domain.model.NotificationType

@Composable
fun NotificationItem(
    notification: Notification,
    modifier: Modifier = Modifier
) {
    val icon = when (notification.type) {
        NotificationType.APPOINTMENT_CONFIRMED -> Icons.Default.CheckCircle
        NotificationType.APPOINTMENT_CANCELLED -> Icons.Default.Info
        NotificationType.SCHEDULE_CHANGED -> Icons.Default.CalendarToday
        NotificationType.GENERAL_INFO -> Icons.Default.Info
    }

    val iconColor = when (notification.type) {
        NotificationType.APPOINTMENT_CONFIRMED -> MedAITheme.colors.status.success
        NotificationType.APPOINTMENT_CANCELLED -> MedAITheme.colors.status.error
        NotificationType.SCHEDULE_CHANGED -> MedAITheme.colors.status.warning
        NotificationType.GENERAL_INFO -> MedAITheme.colors.primary
    }

    val backgroundColor = if (notification.isRead) MedAITheme.colors.surface else MedAITheme.colors.primary.copy(alpha = 0.05f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            MedAIText(
                text = notification.title,
                style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold),
                color = MedAITheme.colors.text.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            MedAIText(
                text = notification.message,
                style = MedAITheme.textStyle.body.small,
                color = MedAITheme.colors.text.secondary
            )
        }

        // New Badge (Optional)
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MedAITheme.colors.primary)
            )
        }
    }
}
