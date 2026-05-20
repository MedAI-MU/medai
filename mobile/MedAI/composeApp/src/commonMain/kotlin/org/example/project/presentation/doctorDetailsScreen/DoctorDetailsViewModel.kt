package org.example.project.presentation.doctorDetailsScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.usecase.doctor.GetDoctorDetailsUseCase

class DoctorDetailsViewModel(
    private val doctorId: String,
    private val getDoctorDetailsUseCase: GetDoctorDetailsUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(DoctorDetailsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<DoctorDetailsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadDoctor()
    }

    private fun loadDoctor() {
        screenModelScope.launch {
            val result = getDoctorDetailsUseCase(doctorId)
            result.fold(
                onSuccess = { doc -> _state.update { it.copy(isLoading = false, doctor = doc) } },
                onFailure = { err ->
                    _state.update { it.copy(isLoading = false, error = err.message) }
                    _effect.send(DoctorDetailsEffect.ShowError("Failed to load doctor info"))
                }
            )
        }
    }

    fun onEvent(event: DoctorDetailsEvent) {
        when(event) {
            DoctorDetailsEvent.BackClicked -> sendEffect(DoctorDetailsEffect.NavigateBack)
            DoctorDetailsEvent.BookClicked -> sendEffect(DoctorDetailsEffect.NavigateToBooking)
            DoctorDetailsEvent.MessageClicked -> {
                val doc = _state.value.doctor
                if (doc != null) {
                    sendEffect(DoctorDetailsEffect.NavigateToChat(doc.id, doc.name))
                }
            }
        }
    }

    private fun sendEffect(effect: DoctorDetailsEffect) {
        screenModelScope.launch { _effect.send(effect) }
    }
}
