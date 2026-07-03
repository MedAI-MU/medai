package org.example.project.presentation.loginScreen

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
    object LoginClicked : LoginEvent()
    object ErrorShown : LoginEvent()
}

sealed class LoginEffect {
    object NavigateToHome : LoginEffect()
    object NavigateToPendingApproval : LoginEffect()
    object NavigateToSignUp : LoginEffect()
    data class ShowError(val message: String) : LoginEffect()
}
