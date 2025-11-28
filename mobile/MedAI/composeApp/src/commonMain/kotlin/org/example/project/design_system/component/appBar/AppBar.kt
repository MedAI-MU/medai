package org.example.project.design_system.component.appBar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme

/**
 * A custom wrapper around Material3 TopAppBar that enforces MedAI styling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedAiAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    centerTitle: Boolean = true
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
        navigationIconContentColor = MedAITheme.colors.text.primary,
        actionIconContentColor = MedAITheme.colors.primary
    )

    if (centerTitle) {
        CenterAlignedTopAppBar(
            title = {
                MedAIText(
                    text = title,
                    style = MedAITheme.textStyle.headline.medium,
                    color = MedAITheme.colors.text.primary
                )
            },
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                }
            },
            actions = actions,
            colors = colors,
            modifier = modifier
        )
    } else {
        TopAppBar(
            title = {
                MedAIText(
                    text = title,
                    style = MedAITheme.textStyle.headline.medium,
                    color = MedAITheme.colors.text.primary
                )
            },
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                }
            },
            actions = actions,
            colors = colors,
            modifier = modifier
        )
    }
}
