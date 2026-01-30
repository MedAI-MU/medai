package org.example.project.presentation.bookingScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.go_home
import medai.composeapp.generated.resources.success_message
import medai.composeapp.generated.resources.success_title
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.MainContainerScreen
import org.jetbrains.compose.resources.stringResource

class BookingSuccessScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        MedAIScaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Success Icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MedAITheme.colors.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MedAITheme.colors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                MedAIText(
                    text = stringResource(Res.string.success_title),
                    style = MedAITheme.textStyle.headline.medium.copy(fontWeight = FontWeight.Bold),
                    color = MedAITheme.colors.text.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                MedAIText(
                    text = stringResource(Res.string.success_message),
                    style = MedAITheme.textStyle.body.large,
                    color = MedAITheme.colors.text.secondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                MedAIButton(
                    text = stringResource(Res.string.go_home),
                    onClick = { navigator.replaceAll(MainContainerScreen()) },
                    variant = ButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
