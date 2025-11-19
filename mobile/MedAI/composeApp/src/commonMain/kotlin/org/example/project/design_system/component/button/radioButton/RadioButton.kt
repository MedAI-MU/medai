package org.example.project.design_system.component.button.radioButton

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MedAIRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null
) {
    val cyanColor = Color(0xFF00E5FF)

    val selectedColor = cyanColor
    val unselectedColor = MedAITheme.colors.text.secondary.copy(alpha = 0.6f)

    val borderColor = if (selected) selectedColor else unselectedColor

    // Animation for the inner circle size
    val dotSize by animateDpAsState(
        targetValue = if (selected) 12.dp else 0.dp,
        animationSpec = tween(durationMillis = 200)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .padding(4.dp)
    ) {
        // The Radio Circle
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // The Inner Dot
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(selectedColor)
            )
        }

        // Optional Label
        if (label != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MedAITheme.textStyle.body.medium,
                color = MedAITheme.colors.text.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MedAIRadioButtonPreview(){
    var selectedOption by remember { mutableStateOf("Option 1") }

    Text(
        "Select Gender",
        style = MedAITheme.textStyle.title.medium,
        color = MedAITheme.colors.text.primary
    )
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        MedAIRadioButton(
            selected = selectedOption == "Male",
            onClick = { selectedOption = "Male" },
            label = "Male"
        )

        MedAIRadioButton(
            selected = selectedOption == "Female",
            onClick = { selectedOption = "Female" },
            label = "Female"
        )
    }
}
