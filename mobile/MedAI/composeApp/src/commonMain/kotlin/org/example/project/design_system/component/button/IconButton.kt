package org.example.project.design_system.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.example.project.design_system.theme.MedAITheme

@Composable
fun MedAICircularButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Same gradient
    val primaryGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF00E5FF),
            MedAITheme.colors.primary
        )
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(primaryGradient)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MedAITheme.colors.text.onPrimary, // White
            modifier = Modifier.size(24.dp)
        )
    }
}
