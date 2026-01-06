package org.example.project.presentation.notificationScreen


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
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.notificationScreen.component.NotificationItem


class NotificationScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<NotificationViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    NotificationEffect.NavigateBack -> navigator.pop()
                }
            }
        }

        MedAIScaffold(
            topBar = {
                MedAiAppBar(
                    title = "Notification",
                    onBackClick = { viewModel.onEvent(NotificationEvent.BackClicked) }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MedAITheme.colors.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MedAITheme.colors.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {

                        items(state.notifications,
                            key = { uiModel ->
                                when (uiModel) {
                                    is NotificationUiModel.Header -> "header_${uiModel.title}"
                                    is NotificationUiModel.Item -> uiModel.notification.id
                                }
                            },

                            contentType = { uiModel ->
                                when (uiModel) {
                                    is NotificationUiModel.Header -> 0
                                    is NotificationUiModel.Item -> 1
                                }
                            }

                        ) { uiModel ->
                            when (uiModel) {
                                is NotificationUiModel.Header -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        MedAIText(
                                            text = uiModel.title,
                                            style = MedAITheme.textStyle.label.large,
                                            color = MedAITheme.colors.text.primary
                                        )
                                    }
                                }
                                is NotificationUiModel.Item -> {
                                    NotificationItem(
                                        notification = uiModel.notification,
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp, vertical = 4.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
