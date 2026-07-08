package org.example.project.design_system.component.header

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.resources.stringResource
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.see_all

@Composable
fun SectionHeader(
    title: String,
    color: Color = MedAITheme.colors.text.primary,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MedAITheme.dimensions.extraLarge, vertical = MedAITheme.dimensions.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MedAIText(
            text = title,
            style = MedAITheme.textStyle.title.large.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        MedAIText(
            text = stringResource(Res.string.see_all),
            style = MedAITheme.textStyle.label.medium,
            color = MedAITheme.colors.primary.copy(alpha = 0.8f),
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}
