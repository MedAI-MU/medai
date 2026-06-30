package org.example.project.presentation.signUpScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.auth.SignUpUseCase

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase
) : MviScreenModel<SignUpState, SignUpEvent, SignUpEffect>(SignUpState()) {

    override fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.FullNameChanged -> setState { copy(fullName = event.value) }
            is SignUpEvent.EmailChanged -> setState { copy(email = event.value) }
            is SignUpEvent.PasswordChanged -> setState { copy(password = event.value) }
            is SignUpEvent.MobileChanged -> setState { copy(mobile = event.value) }
            is SignUpEvent.DateOfBirthChanged -> setState { copy(dob = event.value) }
            is SignUpEvent.RoleChanged -> setState { copy(selectedRole = event.role) }
            is SignUpEvent.ErrorShown -> setState { copy(error = null) }
            is SignUpEvent.SignUpClicked -> performSignUp()
            is SignUpEvent.ToggleDatePicker -> {
                setState { copy(showDatePicker = event.show) }
            }
        }
    }

    private fun performSignUp() {
        val s = state.value
        if (s.isLoading) return

        setState { copy(isLoading = true, error = null) }

        screenModelScope.launch {
            // Call the UseCase
            val result = signUpUseCase(
                fullName = s.fullName,
                email = s.email,
                pass = s.password,
                mobile = s.mobile,
                dob = s.dob,
                role = s.selectedRole
            )

            result.fold(
                onSuccess = {
                    // Success!
                    setState { copy(isLoading = false) }
                    sendEffect(SignUpEffect.NavigateToLogin) // Or NavigateToHome depending on requirements
                },
                onFailure = { error ->
                    setState { copy(isLoading = false, error = error.message ?: "Unknown Error") }
                    sendEffect(SignUpEffect.ShowError(error.message ?: "Unknown Error"))
                }
            )
        }
    }
}
