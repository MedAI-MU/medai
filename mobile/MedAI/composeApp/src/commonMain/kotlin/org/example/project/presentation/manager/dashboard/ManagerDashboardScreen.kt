package org.example.project.presentation.manager.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.manager.ManagedUser

class ManagerDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<ManagerDashboardViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            viewModel.onEvent(ManagerDashboardEvent.Refresh)
        }

        MedAIScaffold(
            topBar = {
                MedAiAppBar(
                    title = "Manager Workspace",
                    centerTitle = false,
                    onBackClick = { navigator.pop() },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(ManagerDashboardEvent.Refresh) }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = MedAITheme.colors.primary
                            )
                        }
                    }
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MedAITheme.colors.background)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Tab selection: Pending vs Approved
                    TabHeader(
                        selectedTab = state.selectedTab,
                        onTabSelected = { viewModel.onEvent(ManagerDashboardEvent.TabSelected(it)) }
                    )

                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

                    // Sub-filter selection: Doctors vs Secretaries
                    RoleFilterHeader(
                        selectedFilter = state.selectedRoleFilter,
                        onFilterSelected = { viewModel.onEvent(ManagerDashboardEvent.RoleFilterSelected(it)) }
                    )

                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

                    // Content listing based on State
                    when (val uiState = state.uiState) {
                        is ManagerUiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MedAITheme.colors.primary)
                            }
                        }
                        is ManagerUiState.Error -> {
                            ErrorStateView(
                                message = uiState.message,
                                onRetry = { viewModel.onEvent(ManagerDashboardEvent.Refresh) }
                            )
                        }
                        is ManagerUiState.Success -> {
                            val activeList = getFilteredList(state)
                            if (activeList.isEmpty()) {
                                EmptyStateView(
                                    tab = state.selectedTab,
                                    role = state.selectedRoleFilter
                                )
                            } else {
                                UserList(
                                    users = activeList,
                                    processingIds = state.processingUserIds,
                                    onApprove = { viewModel.onEvent(ManagerDashboardEvent.ApproveUser(it)) },
                                    onReject = { viewModel.onEvent(ManagerDashboardEvent.RejectUser(it)) },
                                    onRevoke = { viewModel.onEvent(ManagerDashboardEvent.RevokeUser(it)) },
                                    onSelectUser = { viewModel.onEvent(ManagerDashboardEvent.UserSelected(it)) }
                                )
                            }
                        }
                    }
                }

                // Show Details Dialog if selectedUser is not null
                state.selectedUser?.let { user ->
                    UserDetailDialog(
                        user = user,
                        isProcessing = state.processingUserIds.contains(user.id),
                        onApprove = {
                            viewModel.onEvent(ManagerDashboardEvent.ApproveUser(user))
                            viewModel.onEvent(ManagerDashboardEvent.DismissUserDetail)
                        },
                        onReject = {
                            viewModel.onEvent(ManagerDashboardEvent.RejectUser(user))
                            viewModel.onEvent(ManagerDashboardEvent.DismissUserDetail)
                        },
                        onRevoke = {
                            viewModel.onEvent(ManagerDashboardEvent.RevokeUser(user))
                            viewModel.onEvent(ManagerDashboardEvent.DismissUserDetail)
                        },
                        onDismiss = { viewModel.onEvent(ManagerDashboardEvent.DismissUserDetail) }
                    )
                }
            }
        }
    }

    private fun getFilteredList(state: ManagerDashboardState): List<ManagedUser> {
        return when (state.selectedTab) {
            ManagerTab.PENDING -> {
                if (state.selectedRoleFilter == ManagerRoleFilter.DOCTORS) {
                    state.pendingDoctors
                } else {
                    state.pendingSecretaries
                }
            }
            ManagerTab.APPROVED -> {
                if (state.selectedRoleFilter == ManagerRoleFilter.DOCTORS) {
                    state.approvedDoctors
                } else {
                    state.approvedSecretaries
                }
            }
        }
    }
}

@Composable
fun TabHeader(
    selectedTab: ManagerTab,
    onTabSelected: (ManagerTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MedAITheme.dimensions.large)
            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusLarge))
            .background(MedAITheme.colors.surface)
            .border(
                width = 1.dp,
                color = MedAITheme.colors.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(MedAITheme.dimensions.radiusLarge)
            )
            .padding(MedAITheme.dimensions.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.extraSmall)
    ) {
        val tabs = listOf(
            ManagerTab.PENDING to "Pending Registration",
            ManagerTab.APPROVED to "Active Staff"
        )
        tabs.forEach { (tab, title) ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(MedAITheme.dimensions.radiusMedium))
                    .background(
                        if (isSelected) MedAITheme.colors.primary else Color.Transparent
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = MedAITheme.dimensions.medium),
                contentAlignment = Alignment.Center
            ) {
                MedAIText(
                    text = title,
                    style = MedAITheme.textStyle.label.medium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MedAITheme.colors.text.onPrimary else MedAITheme.colors.text.secondary
                )
            }
        }
    }
}

@Composable
fun RoleFilterHeader(
    selectedFilter: ManagerRoleFilter,
    onFilterSelected: (ManagerRoleFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MedAITheme.dimensions.large),
        horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.medium)
    ) {
        val filters = listOf(
            ManagerRoleFilter.DOCTORS to "Doctors",
            ManagerRoleFilter.SECRETARIES to "Secretaries"
        )
        filters.forEach { (filter, title) ->
            val isSelected = selectedFilter == filter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(MedAITheme.dimensions.radiusRound))
                    .background(
                        if (isSelected) MedAITheme.colors.primary.copy(alpha = 0.12f)
                        else MedAITheme.colors.surface
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) MedAITheme.colors.primary else MedAITheme.colors.neutral.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(MedAITheme.dimensions.radiusRound)
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(vertical = MedAITheme.dimensions.small),
                contentAlignment = Alignment.Center
            ) {
                MedAIText(
                    text = title,
                    style = MedAITheme.textStyle.label.medium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) MedAITheme.colors.primary else MedAITheme.colors.text.secondary
                )
            }
        }
    }
}

@Composable
fun UserList(
    users: List<ManagedUser>,
    processingIds: Set<Int>,
    onApprove: (ManagedUser) -> Unit,
    onReject: (ManagedUser) -> Unit,
    onRevoke: (ManagedUser) -> Unit,
    onSelectUser: (ManagedUser) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = MedAITheme.dimensions.large,
            vertical = MedAITheme.dimensions.medium
        ),
        verticalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.medium)
    ) {
        items(users, key = { it.id }) { user ->
            UserCard(
                user = user,
                isProcessing = processingIds.contains(user.id),
                onApprove = { onApprove(user) },
                onReject = { onReject(user) },
                onRevoke = { onRevoke(user) },
                onClick = { onSelectUser(user) }
            )
        }
    }
}

@Composable
fun UserCard(
    user: ManagedUser,
    isProcessing: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onRevoke: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusLarge))
            .clickable(enabled = !isProcessing) { onClick() },
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MedAITheme.dimensions.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Avatar Icon Circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MedAITheme.colors.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                val initials = user.name.split(" ").filter { it.isNotEmpty() }.take(2)
                    .joinToString("") { it.take(1).uppercase() }
                MedAIText(
                    text = initials.ifBlank { "?" },
                    style = MedAITheme.textStyle.title.medium,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.primary
                )
            }

            Spacer(modifier = Modifier.width(MedAITheme.dimensions.large))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                MedAIText(
                    text = user.name,
                    style = MedAITheme.textStyle.title.medium,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.text.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.small)
                ) {
                    // Role Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusSmall))
                            .background(MedAITheme.colors.accent.copy(alpha = 0.15f))
                            .padding(horizontal = MedAITheme.dimensions.small, vertical = 2.dp)
                    ) {
                        MedAIText(
                            text = user.role.replaceFirstChar { it.uppercase() },
                            style = MedAITheme.textStyle.label.small,
                            fontWeight = FontWeight.SemiBold,
                            color = MedAITheme.colors.primary
                        )
                    }
                    // ID Badge
                    MedAIText(
                        text = "ID: ${user.id}",
                        style = MedAITheme.textStyle.label.small,
                        color = MedAITheme.colors.text.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.width(MedAITheme.dimensions.medium))

            // Action Buttons
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MedAITheme.colors.primary,
                    strokeWidth = 2.dp
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.small)) {
                    if (user.status == "pending") {
                        // Approve Button
                        IconButton(
                            onClick = onApprove,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MedAITheme.colors.status.successContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Approve",
                                tint = MedAITheme.colors.status.success,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Reject Button
                        IconButton(
                            onClick = onReject,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MedAITheme.colors.status.errorContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Reject",
                                tint = MedAITheme.colors.status.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        // Revoke Button
                        IconButton(
                            onClick = onRevoke,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MedAITheme.colors.status.errorContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Revoke Access",
                                tint = MedAITheme.colors.status.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    tab: ManagerTab,
    role: ManagerRoleFilter
) {
    val roleStr = if (role == ManagerRoleFilter.DOCTORS) "doctors" else "secretaries"
    val msg = if (tab == ManagerTab.PENDING) {
        "No pending registrations for $roleStr."
    } else {
        "No active $roleStr registered in the system."
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MedAITheme.dimensions.large)
            .background(MedAITheme.colors.surface, shape = RoundedCornerShape(MedAITheme.dimensions.radiusLarge))
            .padding(vertical = 48.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.medium)
        ) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MedAITheme.colors.primary.copy(alpha = 0.4f)
            )
            MedAIText(
                text = msg,
                style = MedAITheme.textStyle.body.large,
                color = MedAITheme.colors.text.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ErrorStateView(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MedAITheme.dimensions.large)
            .background(MedAITheme.colors.status.errorContainer.copy(alpha = 0.2f), shape = RoundedCornerShape(MedAITheme.dimensions.radiusLarge))
            .padding(MedAITheme.dimensions.large),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.medium)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MedAITheme.colors.status.error
            )
            MedAIText(
                text = message,
                style = MedAITheme.textStyle.body.medium,
                color = MedAITheme.colors.status.error,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
            ) {
                MedAIText(
                    text = "Retry",
                    style = MedAITheme.textStyle.label.medium,
                    color = MedAITheme.colors.text.onPrimary
                )
            }
        }
    }
}

@Composable
fun UserDetailDialog(
    user: ManagedUser,
    isProcessing: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onRevoke: () -> Unit,
    onDismiss: () -> Unit
) {
    var showConfirmReject by remember { mutableStateOf(false) }
    var showConfirmRevoke by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MedAITheme.dimensions.medium),
            shape = RoundedCornerShape(MedAITheme.dimensions.radiusLarge),
            colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MedAITheme.dimensions.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Avatar
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MedAITheme.colors.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MedAITheme.colors.primary
                    )
                }

                Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

                MedAIText(
                    text = user.name,
                    style = MedAITheme.textStyle.headline.small,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.text.primary
                )

                Spacer(modifier = Modifier.height(MedAITheme.dimensions.small))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.medium)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusSmall))
                            .background(MedAITheme.colors.primary.copy(alpha = 0.1f))
                            .padding(horizontal = MedAITheme.dimensions.medium, vertical = 2.dp)
                    ) {
                        MedAIText(
                            text = user.role.uppercase(),
                            style = MedAITheme.textStyle.label.small,
                            fontWeight = FontWeight.Bold,
                            color = MedAITheme.colors.primary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusSmall))
                            .background(
                                if (user.status == "approved") MedAITheme.colors.status.successContainer
                                else MedAITheme.colors.status.warningContainer
                            )
                            .padding(horizontal = MedAITheme.dimensions.medium, vertical = 2.dp)
                    ) {
                        MedAIText(
                            text = user.status.uppercase(),
                            style = MedAITheme.textStyle.label.small,
                            fontWeight = FontWeight.Bold,
                            color = if (user.status == "approved") MedAITheme.colors.status.success else MedAITheme.colors.status.warning
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Detail Lines
                DetailRow(label = "User ID", value = user.id.toString())
                DetailRow(label = "Primary Role", value = user.role.replaceFirstChar { it.uppercase() })
                DetailRow(
                    label = "Registration Status",
                    value = user.status.replaceFirstChar { it.uppercase() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Confirmations
                if (showConfirmReject) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.small)
                    ) {
                        MedAIText(
                            text = "Are you sure you want to reject this registration?",
                            style = MedAITheme.textStyle.body.medium,
                            color = MedAITheme.colors.status.error,
                            textAlign = TextAlign.Center
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.medium)) {
                            TextButton(onClick = { showConfirmReject = false }) {
                                MedAIText(
                                    text = "Cancel",
                                    style = MedAITheme.textStyle.label.medium,
                                    color = MedAITheme.colors.text.secondary
                                )
                            }
                            Button(
                                onClick = onReject,
                                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.status.error)
                            ) {
                                MedAIText(
                                    text = "Reject",
                                    style = MedAITheme.textStyle.label.medium,
                                    color = MedAITheme.colors.text.onPrimary
                                )
                            }
                        }
                    }
                } else if (showConfirmRevoke) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.small)
                    ) {
                        MedAIText(
                            text = "Are you sure you want to revoke system access for this user?",
                            style = MedAITheme.textStyle.body.medium,
                            color = MedAITheme.colors.status.error,
                            textAlign = TextAlign.Center
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.medium)) {
                            TextButton(onClick = { showConfirmRevoke = false }) {
                                MedAIText(
                                    text = "Cancel",
                                    style = MedAITheme.textStyle.label.medium,
                                    color = MedAITheme.colors.text.secondary
                                )
                            }
                            Button(
                                onClick = onRevoke,
                                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.status.error)
                            ) {
                                MedAIText(
                                    text = "Revoke",
                                    style = MedAITheme.textStyle.label.medium,
                                    color = MedAITheme.colors.text.onPrimary
                                )
                            }
                        }
                    }
                } else {
                    // Default Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.medium)
                    ) {
                        if (user.status == "pending") {
                            Button(
                                onClick = onApprove,
                                modifier = Modifier.weight(1f),
                                enabled = !isProcessing,
                                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.status.success)
                            ) {
                                MedAIText(
                                    text = "Approve",
                                    style = MedAITheme.textStyle.label.medium,
                                    color = MedAITheme.colors.text.onPrimary
                                )
                            }
                            Button(
                                onClick = { showConfirmReject = true },
                                modifier = Modifier.weight(1f),
                                enabled = !isProcessing,
                                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.status.error)
                            ) {
                                MedAIText(
                                    text = "Reject",
                                    style = MedAITheme.textStyle.label.medium,
                                    color = MedAITheme.colors.text.onPrimary
                                )
                            }
                        } else {
                            Button(
                                onClick = { showConfirmRevoke = true },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isProcessing,
                                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.status.error)
                            ) {
                                MedAIText(
                                    text = "Revoke Access",
                                    style = MedAITheme.textStyle.label.medium,
                                    color = MedAITheme.colors.text.onPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

                    TextButton(onClick = onDismiss, enabled = !isProcessing) {
                        MedAIText(
                            text = "Close",
                            style = MedAITheme.textStyle.label.medium,
                            color = MedAITheme.colors.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MedAITheme.dimensions.small),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MedAIText(
            text = label,
            style = MedAITheme.textStyle.body.medium,
            color = MedAITheme.colors.text.secondary
        )
        MedAIText(
            text = value,
            style = MedAITheme.textStyle.body.medium,
            fontWeight = FontWeight.SemiBold,
            color = MedAITheme.colors.text.primary
        )
    }
}
