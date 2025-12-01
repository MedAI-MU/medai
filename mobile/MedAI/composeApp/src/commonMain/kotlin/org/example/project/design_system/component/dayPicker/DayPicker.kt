package org.example.project.design_system.component.dayPicker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MedAIDateCard(
    day: String,
    weekday: String,
    isSelected: Boolean,
    hasAppointment: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val containerColor = if (isSelected) {
        MedAITheme.colors.surface
    } else {
        Color.Transparent
    }

    val contentColor = if (isSelected) {
        MedAITheme.colors.primary
    } else {
        MedAITheme.colors.text.primary
    }

    val border = if (isSelected) {
        null
    } else {
        BorderStroke(1.dp, MedAITheme.colors.text.primary)
    }

    // 2. The Card Surface
    Surface(
        onClick = onClick,
        modifier = modifier
            .width(52.dp)
            .height(84.dp),
        shape = RoundedCornerShape(50),
        color = containerColor,
        contentColor = contentColor,
        border = border
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            // The Day Number (e.g., "9")
            MedAIText(
                text = day,
                style = MedAITheme.textStyle.headline.medium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            // The Weekday Name (e.g., "MON")
            MedAIText(
                text = weekday.uppercase(),
                style = MedAITheme.textStyle.label.small.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (hasAppointment) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            // If card is selected (white bg), dot is primary color.
                            // If card is unselected (blue/transparent bg), dot is white.
                            color = if (isSelected) MedAITheme.colors.primary else Color.White,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Preview
@Composable
private fun MedAIDateCardPreview(){
    MedAITheme {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                // Unselected state
                MedAIDateCard(
                    day = "9",
                    weekday = "Mon",
                    isSelected = false,
                    hasAppointment = true,
                    onClick = {}
                )
            }
            item {
                // Selected state
                MedAIDateCard(
                    day = "10",
                    weekday = "Tue",
                    isSelected = true,
                    hasAppointment = true,
                    onClick = {}
                )
            }
            item {
                // Unselected state
                MedAIDateCard(
                    day = "11",
                    weekday = "Wed",
                    isSelected = false,
                    onClick = {}
                )
            }
        }
    }
}
