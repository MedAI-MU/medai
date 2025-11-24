package org.example.project.presentation.loginScreen.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.log_in
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back",
                tint = MedAITheme.colors.text.primary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        MedAIText(
            text = stringResource(Res.string.log_in),
            style = MedAITheme.textStyle.headline.medium,
            color = MedAITheme.colors.text.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.padding(24.dp)) // Balance the row
    }
}
