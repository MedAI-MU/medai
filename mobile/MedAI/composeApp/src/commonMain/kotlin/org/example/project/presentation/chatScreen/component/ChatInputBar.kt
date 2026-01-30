package org.example.project.presentation.chatScreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.MedAITheme

@Composable
fun ChatInputBar(value: String, onValueChange: (String) -> Unit, onSendClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(MedAITheme.colors.background).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(onClick = {}) { Icon(Icons.Default.AttachFile, null, tint = MedAITheme.colors.text.secondary) }
        MedAiTextField(value = value, onValueChange = onValueChange, placeholder = "Type message...", modifier = Modifier.weight(1f))
        IconButton(onClick = onSendClick, modifier = Modifier.size(48.dp).clip(CircleShape).background(MedAITheme.colors.primary)) {
            Icon(Icons.AutoMirrored.Filled.Send, null, tint = MedAITheme.colors.text.onPrimary)
        }
    }
}
