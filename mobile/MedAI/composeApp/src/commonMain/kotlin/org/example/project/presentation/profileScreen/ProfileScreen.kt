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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.menu_favorite
import medai.composeapp.generated.resources.menu_help
import medai.composeapp.generated.resources.menu_logout
import medai.composeapp.generated.resources.menu_payment
import medai.composeapp.generated.resources.menu_privacy
import medai.composeapp.generated.resources.menu_profile
import medai.composeapp.generated.resources.menu_settings
import medai.composeapp.generated.resources.my_profile_title
import org.example.project.design_system.component.image.MedAIAsyncImage
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.auth.UserRole
import org.example.project.presentation.loginScreen.LoginScreen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<ProfileViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val dimensions = LocalDimensions.current
        val scrollState = rememberScrollState()

        // Reload profile every time the screen is displayed to fetch edited changes
        LaunchedEffect(Unit) {
            viewModel.loadProfile()
        }

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    ProfileEffect.NavigateBack -> navigator.pop()
                    ProfileEffect.NavigateToLogin -> {
                        val rootNavigator = navigator.parent ?: navigator
                        rootNavigator.replaceAll(LoginScreen())
                    }
                    ProfileEffect.NavigateToEditProfile -> {
                        val rootNavigator = navigator.parent ?: navigator
                        rootNavigator.push(EditProfileScreen())
                    }
                    is ProfileEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                    is ProfileEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = dimensions.extraExtraLarge)
            ) {
                if (state.isLoading && state.user == null) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                } else {
                    // 1. Header Profile & Avatar
                    ProfileHeader(
                        avatarUrl = state.user?.avatarUrl,
                        name = state.user?.name ?: "Guest",
                        email = state.user?.email ?: "",
                        role = state.user?.role?.name ?: "PATIENT",
                        onEditClick = { viewModel.onEvent(ProfileEvent.EditProfileClicked) }
                    )

                    Spacer(modifier = Modifier.height(dimensions.large))

                    // 2. Conditional view of Bio & About for DOCTOR role
                    if (state.user?.role == UserRole.DOCTOR) {
                        DoctorSections(
                            bio = state.user?.bio ?: "No bio available.",
                            about = state.user?.about ?: "No professional details provided."
                        )
                        Spacer(modifier = Modifier.height(dimensions.large))
                    }

                    // 3. Menu Navigation List UI Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = dimensions.extraExtraLarge, topEnd = dimensions.extraExtraLarge))
                            .background(MedAITheme.colors.background)
                            .padding(dimensions.extraLarge)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(dimensions.extraLarge)
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
    }

    @Composable
    fun ProfileHeader(
        avatarUrl: String?,
        name: String,
        email: String,
        role: String,
        onEditClick: () -> Unit
    ) {
        val dimensions = LocalDimensions.current
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dimensions.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .clickable { onEditClick() },
                contentAlignment = Alignment.BottomEnd
            ) {
                // Async image with cache fallback
                MedAIAsyncImage(
                    imageUrl = avatarUrl,
                    nameForInitials = name,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )

                // Edit Pencil Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MedAITheme.colors.primary.copy(alpha = 0.1f))
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = MedAITheme.colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.large))

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

            Spacer(modifier = Modifier.height(dimensions.extraSmall))

            // Display Role chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MedAITheme.colors.primary.copy(alpha = 0.1f))
                    .padding(horizontal = dimensions.medium, vertical = dimensions.extraSmall)
            ) {
                MedAIText(
                    text = role,
                    style = MedAITheme.textStyle.body.small.copy(fontWeight = FontWeight.SemiBold),
                    color = MedAITheme.colors.primary
                )
            }
        }
    }

    @Composable
    fun DoctorSections(bio: String, about: String) {
        val dimensions = LocalDimensions.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.extraLarge),
            verticalArrangement = Arrangement.spacedBy(dimensions.medium)
        ) {
            // Bio Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MedAITheme.colors.primary.copy(alpha = 0.05f))
                    .padding(dimensions.large)
            ) {
                MedAIText(
                    text = "Biography",
                    style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold),
                    color = MedAITheme.colors.primary
                )
                Spacer(modifier = Modifier.height(dimensions.small))
                MedAIText(
                    text = bio,
                    style = MedAITheme.textStyle.body.medium,
                    color = MedAITheme.colors.text.primary
                )
            }

            // About Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MedAITheme.colors.primary.copy(alpha = 0.05f))
                    .padding(dimensions.large)
            ) {
                MedAIText(
                    text = "Professional Details",
                    style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold),
                    color = MedAITheme.colors.primary
                )
                Spacer(modifier = Modifier.height(dimensions.small))
                MedAIText(
                    text = about,
                    style = MedAITheme.textStyle.body.medium,
                    color = MedAITheme.colors.text.primary
                )
            }
        }
    }

    @Composable
    fun ProfileMenuItem(
        icon: ImageVector,
        title: StringResource,
        isDestructive: Boolean = false,
        onClick: () -> Unit
    ) {
        val dimensions = LocalDimensions.current
        val contentColor = if (isDestructive) MedAITheme.colors.status.error else MedAITheme.colors.text.primary
        val iconContainerColor = if (isDestructive) MedAITheme.colors.status.errorContainer else MedAITheme.colors.primary.copy(alpha = 0.1f)
        val iconTint = if (isDestructive) MedAITheme.colors.status.error else MedAITheme.colors.primary

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = dimensions.extraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor)
                    .padding(dimensions.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.width(dimensions.large))

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
                    modifier = Modifier.size(dimensions.large)
                )
            }
        }
    }
}
