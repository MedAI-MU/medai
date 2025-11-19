package org.example.project.design_system.component.dropDown

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MedAIDropdown(
    title: String,
    content: String, // Or @Composable () -> Unit for more complex content
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val shape = RoundedCornerShape(50)

    // Animation for the arrow rotation
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    // Colors & Styles
    val primaryGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF00E5FF),
            MedAITheme.colors.primary
        )
    )

    val backgroundColorModifier = if (expanded) {
        Modifier.background(primaryGradient)
    } else {
        Modifier.background(MedAITheme.colors.surface)
            .border(1.dp, MedAITheme.colors.neutral.copy(alpha = 0.2f), shape)
    }

    val textColor = if (expanded) MedAITheme.colors.text.onPrimary else MedAITheme.colors.primary
    val arrowColor = if (expanded) MedAITheme.colors.text.onPrimary else MedAITheme.colors.primary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(shape)
            .then(backgroundColorModifier)
            .clickable { expanded = !expanded }
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Header Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            MedAIText(
                text = title,
                style = MedAITheme.textStyle.headline.small.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = textColor
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = arrowColor,
                modifier = Modifier.rotate(rotationState)
            )
        }

        // Content (Visible only when expanded)
        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            MedAIText(
                text = content,
                style = MedAITheme.textStyle.body.medium,
                color = textColor.copy(alpha = 0.9f)
            )
        }
    }
}

@Preview
@Composable
fun MedAIDropdownPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        MedAIDropdown(
            title = "What is MedAI?",
            content = "MedAI is an advanced medical assistant powered by artificial intelligence to help you manage appointments and analyze medical scans."
        )

        MedAIDropdown(
            title = "How do I book?",
            content = "Simply navigate to the appointments tab, select a doctor, choose a date, and confirm your slot."
        )
    }
}
