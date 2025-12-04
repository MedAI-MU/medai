package org.example.project.presentation.bookingScreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme

@Composable
fun TimeSlotChip(
    time: String,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MedAITheme.colors.primary
        !isAvailable -> Color.LightGray.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    val borderColor = if (isAvailable && !isSelected) MedAITheme.colors.primary else Color.Transparent
    val textColor = if (isSelected) Color.White else if (!isAvailable) Color.Gray else MedAITheme.colors.primary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .clickable(enabled = isAvailable) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        MedAIText(
            text = time,
            style = MedAITheme.textStyle.label.medium,
            color = textColor
        )
    }
}
