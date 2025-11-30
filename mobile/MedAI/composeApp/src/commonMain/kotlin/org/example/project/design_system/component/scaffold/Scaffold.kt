package org.example.project.design_system.component.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.design_system.theme.MedAITheme

@Composable
fun MedAIScaffold(
    modifier: Modifier = Modifier,
    // --- AppBar Params ---
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    // --- Slots ---
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    containerColor: Color = MedAITheme.colors.background,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit
) {

    val isDark = isSystemInDarkTheme()

    val gradientBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(listOf(Color(0xFF003049), Color(0xFF002639)))
        } else null
    }

    val backgroundModifier = if (gradientBrush != null) {
        Modifier.background(gradientBrush)
    } else {
        Modifier.background(containerColor)
    }

    // If we use the gradient, we must set the Scaffold's container color to Transparent
    // so the gradient (which is applied to the outer Box) shows through.
    val effectiveContainerColor = if (isDark) Color.Transparent else containerColor

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (title != null) {
                MedAiAppBar(
                    title = title,
                    onBackClick = onBackClick,
                    actions = actions
                )
            } else {
                topBar()
            }
        },
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        containerColor = effectiveContainerColor,
        contentColor = MedAITheme.colors.text.primary,
        contentWindowInsets = contentWindowInsets
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(backgroundModifier)
                .padding(paddingValues)
        ) {
            content(PaddingValues(0.dp))
        }
    }
}
