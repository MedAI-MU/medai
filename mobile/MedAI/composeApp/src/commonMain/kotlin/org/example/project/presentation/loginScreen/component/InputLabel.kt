package org.example.project.presentation.loginScreen.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme

@Composable
fun InputLabel(text: String) {
    MedAIText(
        text = text,
        style = MedAITheme.textStyle.title.medium,
        color = MedAITheme.colors.text.primary
    )
    Spacer(modifier = Modifier.height(8.dp))
}
