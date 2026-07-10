package org.example.project.presentation.profileScreen

import org.example.project.domain.model.auth.User

data class EditProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingAvatar: Boolean = false,
    val error: String? = null,

    // Form Inputs
    val name: String = "",
    val phone: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val bio: String = "",
    val about: String = ""
)

sealed class EditProfileEvent {
    object LoadProfile : EditProfileEvent()
    data class NameChanged(val name: String) : EditProfileEvent()
    data class PhoneChanged(val phone: String) : EditProfileEvent()
    data class BirthDateChanged(val birthDate: String) : EditProfileEvent()
    data class GenderChanged(val gender: String) : EditProfileEvent()
    data class BioChanged(val bio: String) : EditProfileEvent()
    data class AboutChanged(val about: String) : EditProfileEvent()
    data class AvatarSelected(val imageBytes: ByteArray, val fileName: String) : EditProfileEvent()
    object SaveClicked : EditProfileEvent()
    object CancelClicked : EditProfileEvent()
}

sealed class EditProfileEffect {
    object NavigateBack : EditProfileEffect()
    data class ShowError(val message: String) : EditProfileEffect()
    data class ShowSuccess(val message: String) : EditProfileEffect()
}
