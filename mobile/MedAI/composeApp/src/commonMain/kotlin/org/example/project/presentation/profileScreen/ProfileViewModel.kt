package org.example.project.presentation.profileScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.repository.ProfileRepository

class ProfileViewModel(
    private val repository: ProfileRepository
) : ScreenModel {

    // Update State to use Domain User
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        screenModelScope.launch {
            repository.getUserProfile().fold(
                onSuccess = { user ->
                    _state.update { it.copy(isLoading = false, user = user) }
                },
                onFailure = { err ->
                    _state.update { it.copy(isLoading = false) }
                }
            )
        }
    }

    fun onEvent(event: ProfileEvent) {
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
            _state.update { it.copy(isLoading = true) }
            repository.logout().fold(
                onSuccess = {
                    sendEffect(ProfileEffect.NavigateToLogin)
                },
                onFailure = {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(ProfileEffect.ShowError("Logout failed"))
                }
            )
        }
    }

    private fun sendEffect(effect: ProfileEffect) {
        screenModelScope.launch { _effect.send(effect) }
    }
}
