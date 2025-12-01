package org.example.project.presentation.homeScreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.Appointment

@Composable
fun HomeAppointmentCard(
    appointments: List<Appointment>,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MedAITheme.colors.primary)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            MedAIText(
                text = "See all",
                style = MedAITheme.textStyle.label.small,
                color = MedAITheme.colors.text.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(12.dp))

        // List
        appointments.take(2).forEach { appointment ->
            AppointmentRow(
                appointment = appointment,
                modifier = Modifier.clickable { onItemClick(appointment.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun AppointmentRow(
    appointment: Appointment,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier, // Apply the click modifier here
        verticalAlignment = Alignment.Top
    ) {
        val dotColor = Color.White
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            MedAIText(
                text = "${appointment.date} - Today",
                style = MedAITheme.textStyle.label.medium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                MedAIText(
                    text = appointment.time,
                    style = MedAITheme.textStyle.body.large,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                MedAIText(
                    text = appointment.doctor.name,
                    style = MedAITheme.textStyle.headline.small,
                    color = Color.White
                )
            }
        }
    }
}
