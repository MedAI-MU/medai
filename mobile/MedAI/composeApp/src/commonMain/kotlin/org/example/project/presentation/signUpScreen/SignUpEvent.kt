package org.example.project.presentation.signUpScreen

sealed class SignUpEvent {
    data class FullNameChanged(val value: String) : SignUpEvent()
    data class PasswordChanged(val value: String) : SignUpEvent()
    data class EmailChanged(val value: String) : SignUpEvent()
    data class MobileChanged(val value: String) : SignUpEvent()
    data class DateOfBirthChanged(val value: String) : SignUpEvent()
    data class ToggleDatePicker(val show: Boolean) : SignUpEvent()  // New Event for showing/hiding date picker

    object SignUpClicked : SignUpEvent()
    object ErrorShown : SignUpEvent()
}
