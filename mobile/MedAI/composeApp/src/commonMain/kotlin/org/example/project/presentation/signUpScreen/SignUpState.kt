package org.example.project.presentation.signUpScreen

data class SignUpState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val mobile: String = "",
    val dob: String = "",
    val showDatePicker: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
