package org.example.project.design_system.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class ButtonVariant {
    Primary,
    Secondary
}

@Composable
fun MedAIButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true
) {
    val primaryGradient = Brush.horizontalGradient(
        colors = listOf(
            MedAITheme.colors.accent,
            MedAITheme.colors.primary
        )
    )

    // Determine styles based on variant
    val backgroundModifier = when (variant) {
        ButtonVariant.Primary -> Modifier.background(primaryGradient)
        ButtonVariant.Secondary -> Modifier.background(MedAITheme.colors.primary.copy(alpha = 0.1f))
    }

    val textColor = when (variant) {
        ButtonVariant.Primary -> MedAITheme.colors.text.onPrimary
        ButtonVariant.Secondary -> MedAITheme.colors.primary
    }

    val shape = RoundedCornerShape(MedAITheme.dimensions.radiusRound)

    Box(
        modifier = modifier
            .heightIn(min = 56.dp)
            .fillMaxWidth()
            .clip(shape)
            .then(backgroundModifier)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = MedAITheme.dimensions.extraLarge),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MedAITheme.textStyle.headline.small.copy(
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MedAIButtonPreview() {
    MedAITheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MedAIButton(
                text = "Log In",
                onClick = { },
                variant = ButtonVariant.Secondary
            )
            MedAIButton(
                text = "Log In",
                onClick = { },
                variant = ButtonVariant.Primary
            )
        }
    }
}
