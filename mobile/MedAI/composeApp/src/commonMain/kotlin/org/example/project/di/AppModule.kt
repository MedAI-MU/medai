package org.example.project.di

import org.example.project.core.domain.ResourceProvider
import org.example.project.core.presentation.util.CalendarManager
import org.example.project.core.presentation.util.ResourceProviderImpl
import org.example.project.data.remote.KtorClient
import org.example.project.data.repository.mock.MockHomeRepository
import org.example.project.data.repository.mock.MockLoginRepository
import org.example.project.data.repository.NetworkSignUpRepository
import org.example.project.data.repository.NetworkSpecialtiesRepository
import org.example.project.data.repository.mock.MockDoctorRepository
import org.example.project.data.repository.mock.MockSpecialtiesRepository
import org.example.project.domain.repository.DoctorRepository
import org.example.project.domain.repository.HomeRepository
import org.example.project.domain.repository.LoginRepository
import org.example.project.domain.repository.SignUpRepository
import org.example.project.domain.repository.SpecialtiesRepository
import org.example.project.domain.usecase.GetAvailableSlotsUseCase
import org.example.project.domain.usecase.GetDoctorDetailsUseCase
import org.example.project.domain.usecase.GetDoctorsUseCase
import org.example.project.domain.usecase.GetHomeDataUseCase
import org.example.project.domain.usecase.GetSpecialtiesUseCase
import org.example.project.domain.usecase.LoginUseCase
import org.example.project.domain.usecase.SignUpUseCase
import org.example.project.presentation.bookingScreen.BookingViewModel
import org.example.project.presentation.doctorDetailsScreen.DoctorDetailsViewModel
import org.example.project.presentation.doctorsScreen.DoctorsListViewModel
import org.example.project.presentation.homeScreen.HomeViewModel
import org.example.project.presentation.loginScreen.LoginViewModel
import org.example.project.presentation.signUpScreen.SignUpViewModel
import org.example.project.presentation.specialtiesScreen.SpecialtiesViewModel

import org.koin.dsl.module

val appModule = module {

    // Utils
    single { CalendarManager() }
    single<ResourceProvider> { ResourceProviderImpl() }

    // --- Network ---
    single { KtorClient.client }

    // --- Repositories ---
    single<LoginRepository> { MockLoginRepository() }
    single<SignUpRepository> { NetworkSignUpRepository(client = get()) }
    single<HomeRepository> { MockHomeRepository() }
    single<SpecialtiesRepository> { MockSpecialtiesRepository() }
    single<DoctorRepository> { MockDoctorRepository() }
    //single<SpecialtiesRepository> { NetworkSpecialtiesRepository(client = get()) }
    //single<LoginRepository> { NetworkLoginRepository(get()) }
    //single<HomeRepository> { NetworkHomeRepository(get()) }

    // --- Use Cases ---
    factory { LoginUseCase(get()) }
    factory { SignUpUseCase(repository = get()) }
    factory { GetHomeDataUseCase(get()) }
    factory { GetSpecialtiesUseCase(get()) }
    factory { GetDoctorsUseCase(get()) }
    factory { GetDoctorDetailsUseCase(get()) }
    factory { GetAvailableSlotsUseCase(get()) }

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
        BookingViewModel(doctorId, get(), get(), get())
    }
}
