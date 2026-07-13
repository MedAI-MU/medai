package org.example.project.di

import org.example.project.data.repository.mock.MockSecretaryRepositoryImpl
import org.example.project.domain.repository.secretary.SecretaryRepository
import org.example.project.domain.usecase.secretary.*
import org.example.project.presentation.secretary.billing.BillingViewModel
import org.example.project.presentation.secretary.dashboard.SecretaryDashboardViewModel
import org.example.project.presentation.secretary.patient.PatientManagementViewModel
import org.example.project.presentation.secretary.queue.QueueManagementViewModel
import org.example.project.presentation.secretary.doctors.SecretaryDoctorListViewModel
import org.example.project.presentation.secretary.schedule.DailyScheduleSummaryViewModel
import org.koin.dsl.module

val secretaryModule = module {
    single<SecretaryRepository> { MockSecretaryRepositoryImpl() }

    single { GetDashboardStatsUseCase(get()) }
    single { GetAllPatientsUseCase(get()) }
    single { CreatePatientUseCase(get()) }
    single { CheckInPatientUseCase(get()) }
    single { GetDoctorQueueUseCase(get()) }
    single { GetAllQueuesUseCase(get()) }
    single { GenerateInvoiceUseCase(get()) }

    factory { SecretaryDashboardViewModel(get(), get(), get(), get()) }
    factory { DailyScheduleSummaryViewModel(get(), get(), get()) }
    factory { SecretaryDoctorListViewModel(get(), get(), get(), get(), get()) }
    factory { PatientManagementViewModel(get(), get()) }
    factory { QueueManagementViewModel(get(), get(), get()) }
    factory { BillingViewModel(get()) }
}
