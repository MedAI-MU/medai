package org.example.project.presentation.signUpScreen

import org.example.project.domain.model.auth.UserRole

data class SignUpState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val mobile: String = "",
    val dob: String = "",
    val selectedRole: UserRole = UserRole.PATIENT,
    val showDatePicker: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class SignUpEvent {
    data class FullNameChanged(val value: String) : SignUpEvent()
    data class PasswordChanged(val value: String) : SignUpEvent()
    data class EmailChanged(val value: String) : SignUpEvent()
    data class MobileChanged(val value: String) : SignUpEvent()
    data class DateOfBirthChanged(val value: String) : SignUpEvent()
    data class RoleChanged(val role: UserRole) : SignUpEvent()
    data class ToggleDatePicker(val show: Boolean) : SignUpEvent()

    object SignUpClicked : SignUpEvent()
    object ErrorShown : SignUpEvent()
}

sealed class SignUpEffect {
    object NavigateToHome : SignUpEffect()
    object NavigateToPendingApproval : SignUpEffect()
    object NavigateToLogin : SignUpEffect()
    data class ShowError(val message: String) : SignUpEffect()
}
