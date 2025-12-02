package org.example.project.di

import org.example.project.core.presentation.util.CalendarManager
import org.example.project.data.remote.KtorClient
import org.example.project.data.repository.MockHomeRepository
import org.example.project.data.repository.MockLoginRepository
import org.example.project.data.repository.NetworkSetPasswordRepository
import org.example.project.data.repository.NetworkSignUpRepository
import org.example.project.domain.repository.HomeRepository
import org.example.project.domain.repository.LoginRepository
import org.example.project.domain.repository.SetPasswordRepository
import org.example.project.domain.repository.SignUpRepository
import org.example.project.domain.usecase.GetHomeDataUseCase
import org.example.project.domain.usecase.LoginUseCase
import org.example.project.domain.usecase.SetPasswordUseCase
import org.example.project.domain.usecase.SignUpUseCase
import org.example.project.presentation.homeScreen.HomeViewModel
import org.example.project.presentation.loginScreen.LoginViewModel
import org.example.project.presentation.setPasswordScreen.SetPasswordViewModel
import org.example.project.presentation.signUpScreen.SignUpViewModel

import org.koin.dsl.module

val appModule = module {

    // Utils
    single { CalendarManager() }

    // --- Network ---
    single { KtorClient.client }

    // --- Repositories ---
    single<LoginRepository> { MockLoginRepository() }
    single<SignUpRepository> { NetworkSignUpRepository(client = get()) }
    single<HomeRepository> { MockHomeRepository() }
    single<SetPasswordRepository> { NetworkSetPasswordRepository(client = get()) }
    //single<LoginRepository> { NetworkLoginRepository(get()) }
    //single<HomeRepository> { NetworkHomeRepository(get()) }

    // --- Use Cases ---
    factory { LoginUseCase(get()) }
    factory { SignUpUseCase(repository = get()) }
    factory { GetHomeDataUseCase(get()) }
    factory { SetPasswordUseCase(repository = get()) }

    // --- ViewModels ---
    factory { LoginViewModel(get()) }
    factory { SignUpViewModel(signUpUseCase = get()) }
    factory { HomeViewModel(get(),get()) }
    factory { SetPasswordViewModel(setPasswordUseCase = get()) }
}
