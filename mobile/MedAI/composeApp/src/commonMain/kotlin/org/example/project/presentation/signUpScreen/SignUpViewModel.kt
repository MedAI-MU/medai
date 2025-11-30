package org.example.project.presentation.signUpScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.usecase.SignUpUseCase

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.FullNameChanged -> _state.update { it.copy(fullName = event.value) }
            is SignUpEvent.EmailChanged -> _state.update { it.copy(email = event.value) }
            is SignUpEvent.PasswordChanged -> _state.update { it.copy(password = event.value) }
            is SignUpEvent.MobileChanged -> _state.update { it.copy(mobile = event.value) }
            is SignUpEvent.DateOfBirthChanged -> _state.update { it.copy(dob = event.value) }
            is SignUpEvent.ErrorShown -> _state.update { it.copy(error = null) }
            is SignUpEvent.SignUpClicked -> performSignUp()
            is SignUpEvent.ToggleDatePicker -> {
                _state.update { it.copy(showDatePicker = event.show) }
            }
        }
    }

    private fun performSignUp() {
        val s = _state.value
        if (s.isLoading) return

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Call the UseCase
            val result = signUpUseCase(
                fullName = s.fullName,
                email = s.email,
                pass = s.password,
                mobile = s.mobile,
                dob = s.dob
            )

            result.fold(
                onSuccess = { response ->
                    // Success!
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message ?: "Unknown Error") }
                }
            )
        }
    }
}
