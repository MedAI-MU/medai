package org.example.project.presentation.loginScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.auth.LoginUseCase

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : MviScreenModel<LoginState, LoginEvent, LoginEffect>(LoginState()) {

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                setState { copy(email = event.value, error = null) }
            }
            is LoginEvent.PasswordChanged -> {
                setState { copy(password = event.value, error = null) }
            }
            is LoginEvent.LoginClicked -> {
                performLogin()
            }
            is LoginEvent.ErrorShown -> {
                setState { copy(error = null) }
            }
        }
    }

    private fun performLogin() {
        val currentState = state.value
        if (currentState.isLoading) return

        setState { copy(isLoading = true) }

        screenModelScope.launch {
            val result = loginUseCase(currentState.email, currentState.password)

            result.fold(
                onSuccess = {
                    setState { copy(isLoading = false) }
                    sendEffect(LoginEffect.NavigateToHome)
                },
                onFailure = { error ->
                    setState { copy(isLoading = false, error = error.message) }
                    sendEffect(LoginEffect.ShowError(error.message ?: "Login failed"))
                }
            )
        }
    }
}
