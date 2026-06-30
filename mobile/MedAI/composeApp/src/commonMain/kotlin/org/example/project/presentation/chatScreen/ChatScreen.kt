package org.example.project.presentation.chatScreen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.chatScreen.component.ChatInputBar
import org.koin.core.parameter.parametersOf

class ChatScreen(private val doctorId: String, private val doctorName: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<ChatViewModel> { parametersOf(doctorId, doctorName) }
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val dimensions = LocalDimensions.current

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    ChatEffect.NavigateBack -> navigator.pop()
                    is ChatEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            topBar = {
                MedAiAppBar(title = state.doctorName, onBackClick = { viewModel.onEvent(ChatEvent.BackClicked) }, actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Call, null, tint = MedAITheme.colors.primary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Videocam, null, tint = MedAITheme.colors.primary) }
                })
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = { ChatInputBar(value = state.messageInput, onValueChange = { viewModel.onEvent(ChatEvent.MessageInputChanged(it)) }, onSendClick = { viewModel.onEvent(ChatEvent.SendMessageClicked) }) },
            containerColor = MedAITheme.colors.background
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), reverseLayout = true, contentPadding = PaddingValues(dimensions.medium)) {
                    if (state.isDoctorTyping) item { MedAIText("Typing...", style = MedAITheme.textStyle.label.small, color = MedAITheme.colors.text.tertiary, modifier = Modifier.padding(start = dimensions.medium)) }
                    items(state.messages) { message -> ChatBubble(message) }
                }
            }
        }
    }
}
