package org.example.project.di

import org.example.project.data.repository.mock.MockSecretaryRepositoryImpl
import org.example.project.domain.repository.secretary.SecretaryRepository
import org.example.project.domain.usecase.secretary.*
import org.example.project.presentation.secretary.billing.BillingViewModel
import org.example.project.presentation.secretary.dashboard.SecretaryDashboardViewModel
import org.example.project.presentation.secretary.patient.PatientManagementViewModel
import org.example.project.presentation.secretary.queue.QueueManagementViewModel
import org.example.project.presentation.secretary.doctors.SecretaryDoctorListViewModel
import org.koin.dsl.module

val secretaryModule = module {
    single<SecretaryRepository> { MockSecretaryRepositoryImpl() }

    single { GetDashboardStatsUseCase(get()) }
    single { GetAllPatientsUseCase(get()) }
    // `CreatePatientUseCase` is in MedicalRecordModule (actually it was provided there, but it's used for patients. Let's make it a factory here if it wasn't in MedicalRecordModule. Wait, in AppModule it was mapped multiple times.)
    // We already moved GetPatientsUseCase, GetPatientByIdUseCase, UpdatePatientUseCase to MedicalRecordModule.
    // So we add CreatePatientUseCase there, or here. Let's keep CreatePatientUseCase, CheckInPatientUseCase, GetDoctorQueueUseCase, GetAllQueuesUseCase, GenerateInvoiceUseCase here.
    single { CreatePatientUseCase(get()) }
    single { CheckInPatientUseCase(get()) }
    single { GetDoctorQueueUseCase(get()) }
    single { GetAllQueuesUseCase(get()) }
    single { GenerateInvoiceUseCase(get()) }

    factory { SecretaryDashboardViewModel(get(), get(), get(), get()) }
    factory { SecretaryDoctorListViewModel(get(), get(), get(), get(), get()) }
    factory { PatientManagementViewModel(get(), get()) }
    factory { QueueManagementViewModel(get(), get()) }
    factory { BillingViewModel(get()) }
}
