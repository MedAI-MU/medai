package org.example.project.di

import org.example.project.core.domain.ResourceProvider
import org.example.project.core.presentation.util.CalendarManager
import org.example.project.core.presentation.util.ResourceProviderImpl
import org.example.project.data.remote.KtorClientFactory
import org.example.project.data.repository.InMemoryUserSessionManager
import org.example.project.data.repository.mock.MockHomeRepository
import org.example.project.data.repository.mock.MockLoginRepository
import org.example.project.data.repository.NetworkLoginRepository
import org.example.project.data.repository.NetworkPatientRepository
import org.example.project.data.repository.NetworkSignUpRepository
import org.example.project.data.repository.mock.MockAppointmentRepository
import org.example.project.data.repository.mock.MockChatRepository
import org.example.project.data.repository.mock.MockDoctorRepository
import org.example.project.data.repository.mock.MockMedicalRecordRepository
import org.example.project.data.repository.mock.MockNotificationRepository
import org.example.project.data.repository.mock.MockProfileRepository
import org.example.project.data.repository.mock.MockSecretaryRepositoryImpl
import org.example.project.data.repository.mock.MockSpecialtiesRepository
import org.example.project.domain.repository.ChatRepository
import org.example.project.domain.repository.AppointmentRepository
import org.example.project.domain.repository.DoctorRepository
import org.example.project.domain.repository.HomeRepository
import org.example.project.domain.repository.LoginRepository
import org.example.project.domain.repository.NotificationRepository
import org.example.project.domain.repository.ProfileRepository
import org.example.project.domain.repository.MedicalRecordRepository
import org.example.project.domain.repository.PatientRepository
import org.example.project.domain.repository.SecretaryRepository
import org.example.project.domain.repository.SignUpRepository
import org.example.project.domain.repository.SpecialtiesRepository
import org.example.project.domain.repository.UserSessionManager
import org.example.project.domain.usecase.BookAppointmentUseCase
import org.example.project.domain.usecase.CancelAppointmentUseCase
import org.example.project.domain.usecase.GetAllergiesUseCase
import org.example.project.domain.usecase.GetAnalysesUseCase
import org.example.project.domain.usecase.GetAnalysisDetailsUseCase
import org.example.project.domain.usecase.GetAppointmentDetailsUseCase
import org.example.project.domain.usecase.GetAppointmentsUseCase
import org.example.project.domain.usecase.GetAvailableSlotsUseCase
import org.example.project.domain.usecase.GetCancelReasonsUseCase
import org.example.project.domain.usecase.GetChatMessagesUseCase
import org.example.project.domain.usecase.GetConversationsUseCase
import org.example.project.domain.usecase.GetDoctorDetailsUseCase
import org.example.project.domain.usecase.GetDoctorsUseCase
import org.example.project.domain.usecase.GetHomeDataUseCase
import org.example.project.domain.usecase.GetMedicalHistoryUseCase
import org.example.project.domain.usecase.GetNotificationsUseCase
import org.example.project.domain.usecase.GetPatientProfileUseCase
import org.example.project.domain.usecase.GetSpecialtiesUseCase
import org.example.project.domain.usecase.GetVaccinationsUseCase
import org.example.project.domain.usecase.LoginUseCase
import org.example.project.domain.usecase.ObserveTypingUseCase
import org.example.project.domain.usecase.SendMessageUseCase
import org.example.project.domain.usecase.SignUpUseCase
import org.example.project.domain.usecase.SubmitReviewUseCase
import org.example.project.domain.usecase.UpdatePatientMetricsUseCase
import org.example.project.presentation.appointmentScreen.AppointmentViewModel
import org.example.project.presentation.bookingScreen.BookingViewModel
import org.example.project.presentation.chatScreen.ChatListViewModel
import org.example.project.presentation.chatScreen.ChatViewModel
import org.example.project.presentation.doctor.prescription.EPrescriptionViewModel
import org.example.project.presentation.doctorDetailsScreen.DoctorDetailsViewModel
import org.example.project.presentation.doctorsScreen.DoctorsListViewModel
import org.example.project.presentation.doctor.dashboard.DoctorDashboardViewModel
import org.example.project.presentation.doctor.chat.DoctorChatListViewModel
import org.example.project.domain.usecase.GetPatientConversationsUseCase
import org.example.project.domain.usecase.GetDoctorAppointmentsUseCase
import org.example.project.domain.usecase.GetPatientByIdUseCase
import org.example.project.domain.usecase.GetPatientsUseCase
import org.example.project.domain.usecase.UpdatePatientUseCase
import org.example.project.domain.usecase.secretary.CheckInPatientUseCase
import org.example.project.domain.usecase.secretary.CreatePatientUseCase
import org.example.project.domain.usecase.secretary.GenerateInvoiceUseCase
import org.example.project.domain.usecase.secretary.GetAllPatientsUseCase
import org.example.project.domain.usecase.secretary.GetAllQueuesUseCase
import org.example.project.domain.usecase.secretary.GetDashboardStatsUseCase
import org.example.project.domain.usecase.secretary.GetDoctorQueueUseCase
import org.example.project.presentation.doctor.records.DoctorPatientRecordsViewModel
import org.example.project.presentation.homeScreen.HomeViewModel
import org.example.project.presentation.loginScreen.LoginViewModel
import org.example.project.presentation.notificationScreen.NotificationViewModel
import org.example.project.presentation.patientDirectory.PatientsDirectoryViewModel
import org.example.project.presentation.profileScreen.ProfileViewModel
import org.example.project.presentation.recordScreen.MedicalRecordViewModel
import org.example.project.presentation.secretary.billing.BillingViewModel
import org.example.project.presentation.secretary.dashboard.SecretaryDashboardViewModel
import org.example.project.presentation.secretary.patient.PatientManagementViewModel
import org.example.project.presentation.secretary.queue.QueueManagementViewModel
import org.example.project.presentation.signUpScreen.SignUpViewModel
import org.example.project.presentation.specialtiesScreen.SpecialtiesViewModel

import org.koin.dsl.module

val appModule = module {

    // Utils
    single { CalendarManager() }
    single<ResourceProvider> { ResourceProviderImpl() }



    // --- Network ---
    // --- Network ---
    single { KtorClientFactory(sessionManager = get()).create() }

    // --- Repositories ---
    single<LoginRepository> { NetworkLoginRepository(get()) }
    single<SignUpRepository> { NetworkSignUpRepository(client = get()) }
    single<HomeRepository> { MockHomeRepository() }
    single<SpecialtiesRepository> { MockSpecialtiesRepository() }
    single<DoctorRepository> { MockDoctorRepository() }
    single<ProfileRepository> { MockProfileRepository() }
    //single<SpecialtiesRepository> { NetworkSpecialtiesRepository(client = get()) }
    //single<LoginRepository> { MockLoginRepository() }
    //single<HomeRepository> { NetworkHomeRepository(get()) }
    single<NotificationRepository> { MockNotificationRepository() }
    single<MedicalRecordRepository> { MockMedicalRecordRepository() }
    single<MedicalRecordRepository> { MockMedicalRecordRepository() }
    single<AppointmentRepository> { MockAppointmentRepository() }

    // --- Patient ---
    single<PatientRepository> {
        NetworkPatientRepository(get())
    }

    // Patient Use Cases
    factory { GetPatientsUseCase(get()) }
    factory { GetPatientByIdUseCase(get()) }
    factory { CreatePatientUseCase(get()) }
    factory { UpdatePatientUseCase(get()) }

    // Patient ViewModel
    factory { PatientsDirectoryViewModel(get()) }

    // --- Secretary ---
    single<SecretaryRepository> {
        MockSecretaryRepositoryImpl()
    }

    // --- Use Cases ---
    single<UserSessionManager> {
        InMemoryUserSessionManager(dataStore = get())
    }
    factory { LoginUseCase(get(),get()) }
    factory { SignUpUseCase(repository = get(), sessionManager = get()) }
    factory { GetHomeDataUseCase(get(), get()) }
    factory { GetSpecialtiesUseCase(get()) }
    factory { GetDoctorsUseCase(get()) }
    factory { GetDoctorDetailsUseCase(get()) }
    factory { GetAvailableSlotsUseCase(get()) }
    factory { BookAppointmentUseCase(get()) }
    factory { GetNotificationsUseCase(get()) }
    factory { GetPatientProfileUseCase(get()) }
    factory { UpdatePatientMetricsUseCase(get()) }
    factory { GetAllergiesUseCase(get()) }
    factory { GetAnalysesUseCase(get()) }
    factory { GetVaccinationsUseCase(get()) }
    factory { GetMedicalHistoryUseCase(get()) }
    factory { GetAnalysisDetailsUseCase(get()) }
    factory { GetAppointmentsUseCase(get()) }
    factory { GetAppointmentDetailsUseCase(get()) }
    factory { CancelAppointmentUseCase(get()) }
    factory { SubmitReviewUseCase(get()) }
    factory { GetCancelReasonsUseCase(get()) }

    // --- ViewModels ---
    factory { LoginViewModel(get()) }
    factory { SignUpViewModel(signUpUseCase = get()) }
    factory { HomeViewModel(get(),get(),get()) }
    factory { SpecialtiesViewModel(get(),get()) }
    factory { (specialtyId: String?) ->
        DoctorsListViewModel(get(), specialtyId)
    }
    factory { (doctorId: String) ->
        DoctorDetailsViewModel(doctorId, get())
    }
    factory { (doctorId: String) ->
        BookingViewModel(doctorId, get(), get(), get(),get())
    }
    factory { ProfileViewModel(get()) }
    factory { NotificationViewModel(get()) }
    factory { MedicalRecordViewModel(get(), get(), get(), get(), get(), get(),get()) }
    factory { AppointmentViewModel(get(), get(), get(), get(), get()) }

    // Doctor
    factory { GetDoctorAppointmentsUseCase(get()) }
    factory { DoctorDashboardViewModel(get(), get()) }
    factory { (patientId: String) ->
        DoctorPatientRecordsViewModel(
            patientId,
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }


    factory { EPrescriptionViewModel() }

    // Chat Feature
    single<ChatRepository> { MockChatRepository() }
//    single<ChatRepository> { NetworkChatRepository(client = get()) }
    factory { GetConversationsUseCase(get()) }
    factory { GetChatMessagesUseCase(get()) }
    factory { SendMessageUseCase(get()) }
    factory { ObserveTypingUseCase(get()) }
    factory { GetPatientConversationsUseCase(get()) }
    factory { DoctorChatListViewModel(get()) }

    factory { ChatListViewModel(get()) }

    // --- Secretary ---
    single { GetDashboardStatsUseCase(get()) }
    single { GetAllPatientsUseCase(get()) }
    single { CreatePatientUseCase(get()) }
    single { CheckInPatientUseCase(get()) }
    single { GetDoctorQueueUseCase(get()) }
    single { GetAllQueuesUseCase(get()) }
    single { GenerateInvoiceUseCase(get()) }

    factory {
        SecretaryDashboardViewModel(
            get(), get(), get(), get()
        )
    }
    factory {
        PatientManagementViewModel(
            get(), get()
        )
    }
    factory {
        QueueManagementViewModel(
            get(), get()
        )
    }
    factory {
        BillingViewModel(
            get()
        )
    }
    factory { (doctorId: String, doctorName: String) ->
        ChatViewModel(
            doctorId,
            doctorName,
            get(),
            get(),
            get()
        )
    }
}
