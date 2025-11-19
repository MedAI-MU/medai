package org.example.project.design_system.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.design_system.theme.MedAITheme

@Composable
fun MedAICardButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary
) {
    // Same gradient as the main button
    val primaryGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF00E5FF),
            MedAITheme.colors.primary
        )
    )

    val backgroundModifier = when (variant) {
        ButtonVariant.Primary -> Modifier.background(primaryGradient)
        ButtonVariant.Secondary -> Modifier.background(MedAITheme.colors.surface)
    }

    val contentColor = when (variant) {
        ButtonVariant.Primary -> MedAITheme.colors.text.onPrimary
        ButtonVariant.Secondary -> MedAITheme.colors.primary // Or Cyan
    }

    Column(
        modifier = modifier
            .width(126.dp)
            .aspectRatio(1f) // Make it a perfect square
            .shadow(
                elevation = if (variant == ButtonVariant.Secondary) 4.dp else 0.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = MedAITheme.colors.primary.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(24.dp))
            .then(backgroundModifier)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(32.dp)
        )
        // Spacing
        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = text,
            style = MedAITheme.textStyle.label.large.copy(
                fontWeight = FontWeight.Bold
            ),
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}
