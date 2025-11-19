package org.example.project.design_system.component.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.example.project.design_system.theme.MedAITheme

@Composable
fun MedAIScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snakeBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MedAITheme.colors.background),

        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            topBar()
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }

            // C. Bottom Bar Area
            Box(modifier = Modifier.fillMaxWidth()) {
                bottomBar()
            }
        }
    }
}
