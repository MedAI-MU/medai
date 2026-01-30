package org.example.project.presentation.chatScreen.component
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.ChatConversation

@Composable
fun ChatListItem(conversation: ChatConversation, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.LightGray))
                if (conversation.isOnline) {
                    Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(MedAITheme.colors.status.success).align(Alignment.BottomEnd))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    MedAIText(text = conversation.doctorName, style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold), color = MedAITheme.colors.text.primary)
                    MedAIText(text = conversation.lastMessageTime, style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.text.secondary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MedAIText(text = conversation.lastMessage, style = MedAITheme.textStyle.body.medium, color = MedAITheme.colors.text.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    if (conversation.unreadCount > 0) {
                        Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(MedAITheme.colors.primary), contentAlignment = Alignment.Center) {
                            MedAIText(text = conversation.unreadCount.toString(), style = MedAITheme.textStyle.label.small, color = Color.White)
                        }
                    }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = MedAITheme.colors.neutral.copy(alpha = 0.1f))
    }
}
