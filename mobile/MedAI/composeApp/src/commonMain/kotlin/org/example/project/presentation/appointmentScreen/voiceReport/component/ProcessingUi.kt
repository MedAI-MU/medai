package org.example.project.presentation.appointmentScreen.voiceReport.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme

@Composable
fun ProcessingUi(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Rotate the loader infinitely
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Dynamic processing message cycling
    var currentMessageIndex by remember { mutableStateOf(0) }
    val messages = listOf(
        "AI is processing audio...",
        "Transcribing speech-to-text (STT)...",
        "Analyzing medical slang and context...",
        "Structuring clinical symptoms...",
        "Generating final medical report..."
    )

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(4000)
            currentMessageIndex = (currentMessageIndex + 1) % messages.size
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MedAITheme.dimensions.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            // Rotating outer ring
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(angle),
                color = MedAITheme.colors.primary,
                trackColor = MedAITheme.colors.primary.copy(alpha = 0.1f),
                strokeWidth = 4.dp
            )

            // Pulsing inner ring
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = 0.9f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .rotate(-angle * 1.5f),
                color = MedAITheme.colors.accent,
                strokeWidth = 3.dp
            )
        }

        Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))

        MedAIText(
            text = "AI Analyzing Consultation",
            style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold),
            color = MedAITheme.colors.text.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        MedAIText(
            text = messages[currentMessageIndex],
            style = MedAITheme.textStyle.body.medium,
            color = MedAITheme.colors.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = MedAITheme.dimensions.large)
        )

        Spacer(modifier = Modifier.height(MedAITheme.dimensions.small))

        MedAIText(
            text = "This may take up to a minute depending on queue and length. You can safely leave this screen; results are saved automatically.",
            style = MedAITheme.textStyle.body.small,
            color = MedAITheme.colors.text.tertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = MedAITheme.dimensions.large)
        )
    }
}
