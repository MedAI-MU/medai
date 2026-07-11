package org.example.project.presentation.appointmentScreen.voiceReport.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme

@Composable
fun RecordingUi(
    durationSeconds: Int,
    onStopClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    val timeString = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MedAITheme.dimensions.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Red Pulse dot & recording label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition()
            val dotAlpha by infiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = 0.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(MedAITheme.colors.status.error.copy(alpha = dotAlpha))
            )
            Spacer(modifier = Modifier.width(MedAITheme.dimensions.small))
            MedAIText(
                text = "Recording Consultation...",
                style = MedAITheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                color = MedAITheme.colors.status.error
            )
        }

        Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

        // Large Timer Text
        MedAIText(
            text = timeString,
            style = MedAITheme.textStyle.headline.large.copy(fontWeight = FontWeight.Bold),
            color = MedAITheme.colors.text.primary
        )

        Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

        // Animated Waveform
        Row(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val barCount = 12
            for (i in 0 until barCount) {
                val infiniteTransition = rememberInfiniteTransition()
                // Offsets so the bars animate asynchronously
                val duration = 400 + (i % 3) * 150
                val barScale by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(duration, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                // Alternate heights
                val maxBarHeight = when (i % 4) {
                    0 -> 40.dp
                    1 -> 25.dp
                    2 -> 48.dp
                    else -> 32.dp
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .width(4.dp)
                        .height(maxBarHeight * barScale)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MedAITheme.colors.status.error.copy(alpha = 0.8f))
                )
            }
        }

        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = MedAITheme.dimensions.large),
            horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.medium)
        ) {
            MedAIButton(
                text = "Cancel",
                onClick = onCancelClick,
                variant = ButtonVariant.Secondary,
                modifier = Modifier.weight(1f)
            )
            MedAIButton(
                text = "Stop & Analyze",
                onClick = onStopClick,
                modifier = Modifier.weight(1.5f)
            )
        }
    }
}
