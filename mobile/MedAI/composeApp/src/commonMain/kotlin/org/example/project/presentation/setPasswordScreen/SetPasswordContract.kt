package org.example.project.presentation.setPasswordScreen

sealed class SetPasswordEvent {
    data class PasswordChanged(val value: String) : SetPasswordEvent()
    data class ConfirmPasswordChanged(val value: String) : SetPasswordEvent()
    data object SubmitClicked : SetPasswordEvent()
}

sealed interface SetPasswordEffect {
    data object NavigateToLogin : SetPasswordEffect
    data class ShowError(val message: String) : SetPasswordEffect
}

data class SetPasswordState(
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false
)
