package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.compose_multiplatform
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.button.MedAICardButton
import org.example.project.design_system.component.button.MedAICircularButton
import org.example.project.design_system.component.textFields.MedAiDateTextField
import org.example.project.design_system.component.textFields.MedAiPasswordTextField
import org.example.project.design_system.component.textFields.MedAiTextArea
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.MedAITheme

@Composable
@Preview
fun App() {
    MedAITheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MedAITheme.colors.background)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text(
                        "Compose: $greeting",
                        color = MedAITheme.colors.text.primary
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MedAITheme.colors.background) // Dark background like your design
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Standard Input
                var email by remember { mutableStateOf("") }
                MedAiTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "example@example.com"
                )

                // 2. Password Input
                var password by remember { mutableStateOf("") }
                MedAiPasswordTextField(
                    value = password,
                    onValueChange = { password = it }
                )

                // 3. Date Input (Visual only for now)
                var date by remember { mutableStateOf("") }
                MedAiDateTextField(
                    value = date,
                    onValueChange = { date = it },
                    placeholder = "DD / MM / YYYY"
                )

                // 4. Text Area
                var reason by remember { mutableStateOf("") }
                MedAiTextArea(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = "Enter Your Reason Here..."
                )

                // 1. Main Buttons
                MedAIButton(
                    text = "Log In",
                    onClick = { },
                    variant = ButtonVariant.Secondary
                )
                MedAIButton(
                    text = "Log In",
                    onClick = { },
                    variant = ButtonVariant.Primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MedAICircularButton(icon = Icons.Default.Favorite, onClick = {})
                    MedAICircularButton(icon = Icons.Default.DateRange, onClick = {})
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MedAICardButton(
                        text = "Ophthalmology",
                        icon = Icons.Default.Favorite, // Replace with Eye icon if available
                        variant = ButtonVariant.Primary,
                        onClick = {}
                    )
                    MedAICardButton(
                        text = "Ophthalmology",
                        icon = Icons.Default.Favorite,
                        variant = ButtonVariant.Secondary,
                        onClick = {}
                    )
                }
            }
        }
    }
}
