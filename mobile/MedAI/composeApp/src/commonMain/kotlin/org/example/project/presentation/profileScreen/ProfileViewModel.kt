package org.example.project.presentation.profileScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.repository.auth.UserSessionManager
import org.example.project.domain.repository.profile.ProfileRepository
import org.example.project.domain.usecase.profile.GetProfileUseCase

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val profileRepository: ProfileRepository,
    private val sessionManager: UserSessionManager
) : MviScreenModel<ProfileState, ProfileEvent, ProfileEffect>(ProfileState()) {

    init {
        loadProfile()
    }

    fun loadProfile() {
        screenModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getProfileUseCase().fold(
                onSuccess = { user ->
                    setState {
                        copy(
                            isLoading = false,
                            user = user
                        )
                    }
                },
                onFailure = { err ->
                    setState { copy(isLoading = false, error = err.message) }
                    sendEffect(ProfileEffect.ShowError("Failed to load profile: ${err.message ?: "Unknown error"}"))
                }
            )
        }
    }

    override fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LoadProfile -> loadProfile()
            ProfileEvent.BackClicked -> sendEffect(ProfileEffect.NavigateBack)
            ProfileEvent.EditProfileClicked -> {
                state.value.user?.let {
                    sendEffect(ProfileEffect.NavigateToEditProfile)
                }
            }
            ProfileEvent.LogoutClicked -> performLogout()
            is ProfileEvent.MenuItemClicked -> handleMenuClick(event.item)
        }
    }

    private fun handleMenuClick(item: ProfileMenuItem) {
        when (item) {
            ProfileMenuItem.Logout -> performLogout()
            ProfileMenuItem.Profile -> {
                state.value.user?.let {
                    sendEffect(ProfileEffect.NavigateToEditProfile)
                }
            }
            else -> {
                // Other menu navigation
            }
        }
    }

    private fun performLogout() {
        screenModelScope.launch {
            setState { copy(isLoading = true) }
            profileRepository.logout().fold(
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
