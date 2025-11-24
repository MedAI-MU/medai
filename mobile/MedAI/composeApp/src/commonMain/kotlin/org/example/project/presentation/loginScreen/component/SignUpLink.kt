package org.example.project.presentation.loginScreen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.dont_have_account
import medai.composeapp.generated.resources.sign_up
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpLink(onSignUpClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        MedAIText(
            text = stringResource(Res.string.dont_have_account),
            color = MedAITheme.colors.text.secondary,
            style = MedAITheme.textStyle.label.medium
        )
        MedAIText(
            text = stringResource(Res.string.sign_up),
            color = MedAITheme.colors.primary,
            style = MedAITheme.textStyle.label.large.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.clickable(onClick = onSignUpClick)
        )
    }
}
