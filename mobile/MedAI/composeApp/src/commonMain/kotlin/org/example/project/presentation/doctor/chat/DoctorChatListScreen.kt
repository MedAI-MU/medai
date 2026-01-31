package org.example.project.presentation.doctor.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.design_system.theme.MedAITheme
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.domain.model.ChatConversation
import org.example.project.presentation.chatScreen.ChatScreen

class DoctorChatListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<DoctorChatListViewModel>()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is DoctorChatListEffect.NavigateToChat -> {
                        // Reusing ChatScreen but passing patientId/Name
                        navigator.push(ChatScreen(effect.patientId, effect.patientName))
                    }
                }
            }
        }

        MedAIScaffold(
            title = "Consultations",
            onBackClick = { navigator.pop() }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(state.conversations) { item ->
                            ConversationItem(
                                conversation = item,
                                onClick = { viewModel.onEvent(DoctorChatListEvent.ConversationClicked(item.doctorId, item.doctorName)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(conversation: ChatConversation, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.doctorName,
                style = MedAITheme.textStyle.title.medium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = conversation.lastMessage,
                style = MedAITheme.textStyle.body.medium,
                color = MedAITheme.colors.text.secondary,
                maxLines = 1
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = conversation.lastMessageTime,
                style = MedAITheme.textStyle.label.small,
                color = MedAITheme.colors.text.tertiary
            )
            if (conversation.unreadCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                // Badge could go here
                Text(
                    text = "${conversation.unreadCount} new",
                    style = MedAITheme.textStyle.label.small,
                    color = MedAITheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
