package org.example.project.presentation.profileScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.menu_favorite
import medai.composeapp.generated.resources.menu_help
import medai.composeapp.generated.resources.menu_logout
import medai.composeapp.generated.resources.menu_payment
import medai.composeapp.generated.resources.menu_privacy
import medai.composeapp.generated.resources.menu_profile
import medai.composeapp.generated.resources.menu_settings
import medai.composeapp.generated.resources.my_profile_title
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.loginScreen.LoginScreen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<ProfileViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when(effect) {
                    ProfileEffect.NavigateBack -> navigator.pop()
                    ProfileEffect.NavigateToLogin -> navigator.replaceAll(LoginScreen())
                    is ProfileEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                    is ProfileEffect.NavigateToScreen -> { /* Handle generic nav */ }
                }
            }
        }

        MedAIScaffold(
            title = stringResource(Res.string.my_profile_title),
            onBackClick = { viewModel.onEvent(ProfileEvent.BackClicked) },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // 1. Header Profile Info
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    ProfileHeader(
                        name = state.user?.name ?: "Guest",
                        email = state.user?.email ?: "",
                        onEditClick = { viewModel.onEvent(ProfileEvent.EditProfileClicked) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. White Menu List
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        ProfileMenuItem(
                            icon = Icons.Default.Person,
                            title = Res.string.menu_profile,
                            onClick = { viewModel.onEvent(ProfileEvent.MenuItemClicked(ProfileMenuItem.Profile)) }
                        )
                        ProfileMenuItem(
                            icon = Icons.Default.Favorite,
                            title = Res.string.menu_favorite,
                            onClick = { viewModel.onEvent(ProfileEvent.MenuItemClicked(ProfileMenuItem.Favorite)) }
                        )
                        ProfileMenuItem(
                            icon = Icons.Default.Payment,
                            title = Res.string.menu_payment,
                            onClick = { viewModel.onEvent(ProfileEvent.MenuItemClicked(ProfileMenuItem.Payment)) }
                        )
                        ProfileMenuItem(
                            icon = Icons.Default.Lock,
                            title = Res.string.menu_privacy,
                            onClick = { viewModel.onEvent(ProfileEvent.MenuItemClicked(ProfileMenuItem.Privacy)) }
                        )
                        ProfileMenuItem(
                            icon = Icons.Default.Settings,
                            title = Res.string.menu_settings,
                            onClick = { viewModel.onEvent(ProfileEvent.MenuItemClicked(ProfileMenuItem.Settings)) }
                        )
                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.Help,
                            title = Res.string.menu_help,
                            onClick = { viewModel.onEvent(ProfileEvent.MenuItemClicked(ProfileMenuItem.Help)) }
                        )

                        // Logout
                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            title = Res.string.menu_logout,
                            isDestructive = true,
                            onClick = { viewModel.onEvent(ProfileEvent.LogoutClicked) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ProfileHeader(name: String, email: String, onEditClick: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray) // Placeholder for AsyncImage
                )

                // Edit Pencil Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .offset(x = 4.dp, y = 4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onEditClick() }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MedAITheme.colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            MedAIText(
                text = name,
                style = MedAITheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold),
                color = MedAITheme.colors.text.primary
            )

            MedAIText(
                text = email,
                style = MedAITheme.textStyle.body.medium,
                color = MedAITheme.colors.text.secondary
            )
            // Phone could go here if available in User model
        }
    }

    @Composable
    fun ProfileMenuItem(
        icon: ImageVector,
        title: StringResource,
        isDestructive: Boolean = false,
        onClick: () -> Unit
    ) {
        val contentColor = if (isDestructive) MedAITheme.colors.status.error else MedAITheme.colors.text.primary
        val iconContainerColor = if (isDestructive) MedAITheme.colors.status.errorContainer else MedAITheme.colors.primary.copy(alpha = 0.1f) // Light Cyan
        val iconTint = if (isDestructive) MedAITheme.colors.status.error else MedAITheme.colors.primary // Brand Cyan

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            MedAIText(
                text = stringResource(title),
                style = MedAITheme.textStyle.title.medium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

            if (!isDestructive) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MedAITheme.colors.text.secondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
