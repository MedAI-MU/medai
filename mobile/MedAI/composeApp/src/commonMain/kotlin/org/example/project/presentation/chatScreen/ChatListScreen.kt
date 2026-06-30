package org.example.project.presentation.chatScreen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.chatScreen.component.ChatListItem

class ChatListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<ChatListViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val dimensions = LocalDimensions.current

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ChatListEffect.NavigateToChat -> {
                        navigator.parent?.push(ChatScreen(effect.doctorId, effect.doctorName)) ?: navigator.push(ChatScreen(effect.doctorId, effect.doctorName))
                    }
                    is ChatListEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        MedAIScaffold(
            title = "Messages",
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MedAITheme.colors.primary,
            contentWindowInsets = WindowInsets.statusBars
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(dimensions.large))
                Box(
                    modifier = Modifier.fillMaxSize()
                        .clip(RoundedCornerShape(topStart = dimensions.extraExtraLarge, topEnd = dimensions.extraExtraLarge))
                        .background(MedAITheme.colors.background)
                        .padding(top = dimensions.large)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MedAITheme.colors.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (state.conversations.isEmpty()) {
                        MedAIText(
                            text = "No messages yet",
                            style = MedAITheme.textStyle.body.large,
                            color = MedAITheme.colors.text.tertiary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                        ) {
                            items(state.conversations) { conversation ->
                                ChatListItem(
                                    conversation = conversation,
                                    onClick = {
                                        viewModel.onEvent(
                                            ChatListEvent.ConversationClicked(
                                                conversation.doctorId,
                                                conversation.doctorName
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
