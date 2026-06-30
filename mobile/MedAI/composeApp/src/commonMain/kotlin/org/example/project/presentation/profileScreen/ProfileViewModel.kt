package org.example.project.presentation.profileScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.repository.profile.ProfileRepository

class ProfileViewModel(
    private val repository: ProfileRepository
) : MviScreenModel<ProfileState, ProfileEvent, ProfileEffect>(ProfileState()) {

    init {
        loadProfile()
    }

    private fun loadProfile() {
        screenModelScope.launch {
            repository.getUserProfile().fold(
                onSuccess = { user ->
                    setState { copy(isLoading = false, user = user) }
                },
                onFailure = { err ->
                    setState { copy(isLoading = false, error = err.message) }
                    sendEffect(ProfileEffect.ShowError("Failed to load profile"))
                }
            )
        }
    }

    override fun onEvent(event: ProfileEvent) {
        when(event) {
            ProfileEvent.BackClicked -> sendEffect(ProfileEffect.NavigateBack)
            ProfileEvent.EditProfileClicked -> { /* Navigate to Edit Profile */ }
            ProfileEvent.LogoutClicked -> performLogout()
            is ProfileEvent.MenuItemClicked -> handleMenuClick(event.item)
        }
    }

    private fun handleMenuClick(item: ProfileMenuItem) {
        when(item) {
            ProfileMenuItem.Logout -> performLogout()
            else -> {
                // Navigate to specific screens (Placeholders)
            }
        }
    }

    private fun performLogout() {
        screenModelScope.launch {
            setState { copy(isLoading = true) }
            repository.logout().fold(
                onSuccess = {
                    sendEffect(ProfileEffect.NavigateToLogin)
                },
                onFailure = {
                    setState { copy(isLoading = false) }
                    sendEffect(ProfileEffect.ShowError("Logout failed"))
                }
            )
        }
    }
}
