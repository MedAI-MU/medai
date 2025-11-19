package org.example.project.design_system.component.textFields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.project.design_system.theme.MedAITheme

@Composable
fun MedAISearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search..."
) {
    MedAiTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = placeholder,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MedAITheme.colors.primary
            )
        }
    )
}
