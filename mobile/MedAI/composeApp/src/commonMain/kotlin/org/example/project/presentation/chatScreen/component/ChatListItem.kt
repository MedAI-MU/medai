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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.chat.ChatConversation

@Composable
fun ChatListItem(conversation: ChatConversation, onClick: () -> Unit) {
    val dimensions = LocalDimensions.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = dimensions.extraLarge, vertical = dimensions.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Box(modifier = Modifier.size(dimensions.spacing48 + dimensions.small).clip(CircleShape).background(MedAITheme.colors.neutral))
                if (conversation.isOnline) {
                    Box(modifier = Modifier.size(dimensions.small + dimensions.extraSmall).clip(CircleShape).background(MedAITheme.colors.status.success).align(Alignment.BottomEnd))
                }
            }
            Spacer(modifier = Modifier.width(dimensions.large))
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    MedAIText(text = conversation.doctorName, style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold), color = MedAITheme.colors.text.primary)
                    MedAIText(text = conversation.lastMessageTime, style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.text.secondary)
                }
                Spacer(modifier = Modifier.height(dimensions.extraSmall))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MedAIText(text = conversation.lastMessage, style = MedAITheme.textStyle.body.medium, color = MedAITheme.colors.text.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    if (conversation.unreadCount > 0) {
                        Box(modifier = Modifier.size(dimensions.extraLarge + dimensions.extraSmall).clip(CircleShape).background(MedAITheme.colors.primary), contentAlignment = Alignment.Center) {
                            MedAIText(text = conversation.unreadCount.toString(), style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.onPrimary)
                        }
                    }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = dimensions.extraLarge), color = MedAITheme.colors.neutral.copy(alpha = 0.1f))
    }
}
