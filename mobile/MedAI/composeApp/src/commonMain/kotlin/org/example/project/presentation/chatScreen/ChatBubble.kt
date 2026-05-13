package org.example.project.presentation.chatScreen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.chat.Message

@Composable
fun ChatBubble(message: Message) {
    val isUser = message.isFromUser
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val containerColor = if (isUser) MedAITheme.colors.primary else MedAITheme.colors.surface
    val textColor = if (isUser) MedAITheme.colors.text.onPrimary else MedAITheme.colors.text.primary
    val time = message.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val timeString = "${time.hour}:${time.minute.toString().padStart(2, '0')}"

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), contentAlignment = alignment) {
        Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
            Box(
                modifier = Modifier.widthIn(max = 280.dp)
                    .background(containerColor, RoundedCornerShape(16.dp, 16.dp, if (isUser) 2.dp else 16.dp, if (isUser) 16.dp else 2.dp))
                    .padding(12.dp)
            ) {
                MedAIText(text = message.text, style = MedAITheme.textStyle.body.medium, color = textColor)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                MedAIText(text = timeString, style = MedAITheme.textStyle.label.small.copy(fontSize = 10.sp), color = MedAITheme.colors.text.tertiary)
                if (isUser) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Read", tint = MedAITheme.colors.primary, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}
