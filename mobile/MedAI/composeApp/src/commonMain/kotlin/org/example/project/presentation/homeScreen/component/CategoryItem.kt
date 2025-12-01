package org.example.project.presentation.homeScreen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.example.project.core.presentation.util.UiText
import org.example.project.core.presentation.util.asString
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.icons.IconMapper
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.Category

@Composable
fun CategoryItem(
    category: Category,
    onClick: () -> Unit
) {
    // Mapping string names to Icons for Mocking purposes.
    // In real app, these would be DrawableResources.
    val icon = IconMapper.getCategoryIcon(category.iconName)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MedAITheme.colors.primary, // Teal color
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        MedAIText(
            text = UiText.StringRes(category.title).asString(),
            style = MedAITheme.textStyle.label.medium,
            color = MedAITheme.colors.primary
        )
    }
}
