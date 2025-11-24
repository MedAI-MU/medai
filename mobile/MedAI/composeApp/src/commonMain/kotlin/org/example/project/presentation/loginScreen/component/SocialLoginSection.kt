package org.example.project.presentation.loginScreen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.or_sign_up_with
import org.example.project.design_system.component.button.MedAICircularButton
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.icons.MedAIIcons
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SocialLoginSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        MedAIText(
            text = stringResource(Res.string.or_sign_up_with),
            style = MedAITheme.textStyle.label.medium,
            color = MedAITheme.colors.text.secondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MedAICircularButton(icon = MedAIIcons.Google, onClick = {})
            Spacer(modifier = Modifier.width(width = 16.dp))
            MedAICircularButton(icon = MedAIIcons.Facebook, onClick = {})
            Spacer(modifier = Modifier.width(16.dp))
            MedAICircularButton(icon = MedAIIcons.Fingerprint, onClick = {})
        }
    }
}
