package org.example.project.di

import org.example.project.data.remote.KtorClient
import org.example.project.data.repository.MockLoginRepository
import org.example.project.data.repository.NetworkSignUpRepository
import org.example.project.domain.repository.LoginRepository
import org.example.project.domain.repository.SignUpRepository
import org.example.project.domain.usecase.LoginUseCase
import org.example.project.domain.usecase.SignUpUseCase
import org.example.project.presentation.loginScreen.LoginViewModel
import org.example.project.presentation.signUpScreen.SignUpViewModel

import org.koin.dsl.module

val appModule = module {
    // --- Network ---
    single { KtorClient.client }

    // --- Repositories ---
    single<LoginRepository> { MockLoginRepository() }
    single<SignUpRepository> { NetworkSignUpRepository(client = get()) }

    //single<LoginRepository> { NetworkLoginRepository(get()) }

    // --- Use Cases ---
    factory { LoginUseCase(get()) }
    factory { SignUpUseCase(repository = get()) }

    // --- ViewModels ---
    factory { LoginViewModel(get()) }
    factory { SignUpViewModel(signUpUseCase = get()) }
}
