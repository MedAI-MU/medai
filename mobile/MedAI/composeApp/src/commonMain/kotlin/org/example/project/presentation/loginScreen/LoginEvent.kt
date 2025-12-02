package org.example.project.presentation.loginScreen

sealed class LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
    object LoginClicked : LoginEvent()
    object ErrorShown : LoginEvent() // Reset error after showing snackbar
    object ForgotPasswordClicked : LoginEvent()
    object SignUpClicked : LoginEvent()

}

sealed interface LoginEffect {
    object NavigateToHome : LoginEffect
    object NavigateToForgotPassword : LoginEffect //Navigates to SetPasswordScreen
    data class ShowError(val message: String) : LoginEffect
    object NavigateToSignUp : LoginEffect
}
