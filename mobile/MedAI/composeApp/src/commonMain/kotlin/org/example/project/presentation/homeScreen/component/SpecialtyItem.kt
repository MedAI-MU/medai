package org.example.project.presentation.homeScreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.core.presentation.util.UiText
import org.example.project.core.presentation.util.asString
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.icons.IconMapper
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.Specialty

@Composable
fun SpecialtyItem(
    specialty: Specialty,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = IconMapper.getSpecialtyIcon(specialty.iconName)
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(MedAITheme.colors.primary.copy(alpha = 0.85f)) // Use Primary Blue
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        MedAIText(
            text = UiText.StringRes(specialty.title).asString(),
            style = MedAITheme.textStyle.label.medium,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
