package org.example.project.presentation.loginScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.usecase.LoginUseCase

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.update { it.copy(email = event.value, error = null) }
            }
            is LoginEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.value, error = null) }
            }
            is LoginEvent.LoginClicked -> {
                performLogin()
            }
            is LoginEvent.ErrorShown -> {
                _state.update { it.copy(error = null) }
            }
            is LoginEvent.ForgotPasswordClicked -> {
                sendEffect(LoginEffect.NavigateToForgotPassword)
            }

            LoginEvent.SignUpClicked -> {
                sendEffect(LoginEffect.NavigateToSignUp)
            }

        }
    }

    private fun sendEffect(effect: LoginEffect) {
        screenModelScope.launch {
            _effect.send(effect)
        }
    }

    private fun performLogin() {
        val currentState = _state.value
        if (currentState.isLoading) return

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = loginUseCase(currentState.email, currentState.password)

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }
}
