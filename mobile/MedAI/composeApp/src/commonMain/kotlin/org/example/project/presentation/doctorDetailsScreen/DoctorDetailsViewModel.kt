package org.example.project.presentation.doctorDetailsScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.doctor.GetDoctorDetailsUseCase

class DoctorDetailsViewModel(
    private val doctorId: String,
    private val getDoctorDetailsUseCase: GetDoctorDetailsUseCase
) : MviScreenModel<DoctorDetailsState, DoctorDetailsEvent, DoctorDetailsEffect>(DoctorDetailsState()) {

    init {
        loadDoctor()
    }

    private fun loadDoctor() {
        screenModelScope.launch {
            val result = getDoctorDetailsUseCase(doctorId)
            result.fold(
                onSuccess = { doc -> setState { copy(isLoading = false, doctor = doc) } },
                onFailure = { err ->
                    setState { copy(isLoading = false, error = err.message) }
                    sendEffect(DoctorDetailsEffect.ShowError("Failed to load doctor info"))
                }
            )
        }
    }

    override fun onEvent(event: DoctorDetailsEvent) {
        when(event) {
            DoctorDetailsEvent.BackClicked -> sendEffect(DoctorDetailsEffect.NavigateBack)
            DoctorDetailsEvent.BookClicked -> sendEffect(DoctorDetailsEffect.NavigateToBooking)
            DoctorDetailsEvent.MessageClicked -> {
                val doc = state.value.doctor
                if (doc != null) {
                    sendEffect(DoctorDetailsEffect.NavigateToChat(doc.id, doc.name))
                }
            }
        }
    }
}
