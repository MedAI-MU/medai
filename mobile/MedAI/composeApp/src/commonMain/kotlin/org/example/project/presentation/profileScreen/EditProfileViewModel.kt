package org.example.project.presentation.profileScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.profile.GetProfileUseCase
import org.example.project.domain.usecase.profile.UpdateProfileUseCase
import org.example.project.domain.usecase.profile.UploadAvatarUseCase

class EditProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase
) : MviScreenModel<EditProfileState, EditProfileEvent, EditProfileEffect>(EditProfileState()) {

    override fun onEvent(event: EditProfileEvent) {
        when (event) {
            EditProfileEvent.LoadProfile -> loadProfile()
            is EditProfileEvent.NameChanged -> setState { copy(name = event.name) }
            is EditProfileEvent.PhoneChanged -> setState { copy(phone = event.phone) }
            is EditProfileEvent.BirthDateChanged -> setState { copy(birthDate = event.birthDate) }
            is EditProfileEvent.GenderChanged -> setState { copy(gender = event.gender) }
            is EditProfileEvent.BioChanged -> setState { copy(bio = event.bio) }
            is EditProfileEvent.AvatarSelected -> uploadAvatar(event.imageBytes, event.fileName)
            EditProfileEvent.SaveClicked -> saveProfile()
            EditProfileEvent.CancelClicked -> sendEffect(EditProfileEffect.NavigateBack)
        }
    }

    private fun loadProfile() {
        screenModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getProfileUseCase().fold(
                onSuccess = { user ->
                    setState {
                        copy(
                            isLoading = false,
                            user = user,
                            name = user.name,
                            phone = user.phoneNumber ?: "",
                            birthDate = user.birthDate ?: "",
                            gender = user.gender ?: "",
                            bio = user.bio ?: "",
                            about = user.about ?: ""
                        )
                    }
                },
                onFailure = { err ->
                    setState { copy(isLoading = false, error = err.message) }
                    sendEffect(EditProfileEffect.ShowError("Failed to load profile details: ${err.message ?: "Unknown error"}"))
                }
            )
        }
    }

    private fun saveProfile() {
        val currentUser = state.value.user ?: return
        val name = state.value.name.trim()
        val phone = state.value.phone.trim()
        val birthDate = state.value.birthDate.trim()
        val gender = state.value.gender.trim()
        val bio = state.value.bio.trim()

        if (name.length < 5 || name.length > 100) {
            sendEffect(EditProfileEffect.ShowError("Name must be between 5 and 100 characters"))
            return
        }

        val phoneRegex = Regex("^(010|011|012|015)\\d{8}$")
        if (phone.isNotEmpty() && !phoneRegex.matches(phone)) {
            sendEffect(EditProfileEffect.ShowError("Phone must start with 010, 011, 012 or 015 followed by 8 digits"))
            return
        }

        screenModelScope.launch {
            setState { copy(isSaving = true) }
            updateProfileUseCase(
                userId = currentUser.id,
                name = name,
                phone = phone,
                birthDate = birthDate.ifBlank { null },
                gender = gender.ifBlank { null },
                bio = bio.ifBlank { null }
            ).fold(
                onSuccess = { updatedUser ->
                    setState { copy(isSaving = false) }
                    sendEffect(EditProfileEffect.ShowSuccess("Profile updated successfully"))
                    sendEffect(EditProfileEffect.NavigateBack)
                },
                onFailure = { err ->
                    setState { copy(isSaving = false) }
                    sendEffect(EditProfileEffect.ShowError("Update failed: ${err.message ?: "Unknown error"}"))
                }
            )
        }
    }

    private fun uploadAvatar(imageBytes: ByteArray, fileName: String) {
        val currentUser = state.value.user ?: return
        screenModelScope.launch {
            setState { copy(isUploadingAvatar = true) }
            uploadAvatarUseCase(
                userId = currentUser.id,
                imageBytes = imageBytes,
                fileName = fileName
            ).fold(
                onSuccess = { avatarUrl ->
                    setState {
                        copy(
                            isUploadingAvatar = false,
                            user = state.value.user?.copy(avatarUrl = avatarUrl)
                        )
                    }
                    sendEffect(EditProfileEffect.ShowSuccess("Avatar updated successfully"))
                },
                onFailure = { err ->
                    setState { copy(isUploadingAvatar = false) }
                    sendEffect(EditProfileEffect.ShowError("Avatar upload failed: ${err.message ?: "Unknown error"}"))
                }
            )
        }
    }
}
