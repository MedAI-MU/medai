package org.example.project.di

import org.example.project.data.repository.NetworkAppointmentRepository
import org.example.project.domain.repository.appointment.AppointmentRepository
import org.example.project.domain.usecase.appointment.*
import org.example.project.presentation.appointmentScreen.AppointmentViewModel
import org.example.project.presentation.bookingScreen.BookingViewModel
import org.koin.dsl.module

val appointmentModule = module {
    single<AppointmentRepository> { NetworkAppointmentRepository(get()) }

    factory { BookAppointmentUseCase(get(), get()) }
    factory { GetAvailableSlotsUseCase(get()) }
    factory { GetAppointmentsUseCase(get()) }
    factory { GetAppointmentDetailsUseCase(get()) }
    factory { CancelAppointmentUseCase(get()) }
    factory { SubmitReviewUseCase(get()) }
    factory { GetCancelReasonsUseCase(get()) }
    factory { GetDoctorAppointmentsUseCase(get()) }
    factory { GetTodayAppointmentsUseCase(get()) }

    factory { AppointmentViewModel(get(), get(), get(), get(), get(), get()) }
    factory { (doctorId: String) ->
        BookingViewModel(doctorId, get(), get(), get(), get())
    }
}
