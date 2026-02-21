package org.example.project.presentation.signUpScreen.component
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.new_account
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpTopBar(onBackClick: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Title
        MedAIText(
            text = stringResource(Res.string.new_account),
            style = MedAITheme.textStyle.headline.medium,
            color = MedAITheme.colors.text.primary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
