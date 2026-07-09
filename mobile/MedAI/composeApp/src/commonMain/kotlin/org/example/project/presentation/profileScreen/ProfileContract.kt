package org.example.project.presentation.profileScreen

import org.example.project.domain.model.auth.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

sealed class ProfileEvent {
    object LoadProfile : ProfileEvent()
    object EditProfileClicked : ProfileEvent()

    // Navigation / Actions
    object LogoutClicked : ProfileEvent()
    data class MenuItemClicked(val item: ProfileMenuItem) : ProfileEvent()
    object BackClicked : ProfileEvent()
}

sealed class ProfileEffect {
    object NavigateToLogin : ProfileEffect()
    object NavigateBack : ProfileEffect()
    object NavigateToEditProfile : ProfileEffect()
    data class NavigateToScreen(val route: String) : ProfileEffect()
    data class ShowError(val message: String) : ProfileEffect()
    data class ShowSuccess(val message: String) : ProfileEffect()
}

enum class ProfileMenuItem {
    Profile, Favorite, Payment, Privacy, Settings, Help, Logout
}
