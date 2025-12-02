package org.example.project.presentation.setPasswordScreen


import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.usecase.SetPasswordUseCase

class SetPasswordViewModel(
    private val setPasswordUseCase: SetPasswordUseCase
) : ScreenModel {

    // UI State (Persistent)
    private val _state = MutableStateFlow(SetPasswordState())
    val state = _state.asStateFlow()

    // Side Effects (One-shot events)
    private val _effect = Channel<SetPasswordEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: SetPasswordEvent) {
        when (event) {
            is SetPasswordEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.value) }
            }
            is SetPasswordEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.value) }
            }
            is SetPasswordEvent.SubmitClicked -> submitPassword()
        }
    }

    private fun submitPassword() {
        val currentState = _state.value

        // Prevent double clicks
        if (currentState.isLoading) return

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Delegate to UseCase
            val result = setPasswordUseCase(
                password = currentState.password,
                confirmPassword = currentState.confirmPassword
            )

            _state.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = {
                    // Navigate to Login on success
                    _effect.send(SetPasswordEffect.NavigateToLogin)
                },
                onFailure = { error ->
                    // Show error message
                    _effect.send(SetPasswordEffect.ShowError(error.message ?: "Unknown Error"))
                }
            )
        }
    }
}
