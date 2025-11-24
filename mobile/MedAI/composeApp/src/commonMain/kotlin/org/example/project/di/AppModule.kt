package org.example.project.di

import org.example.project.data.remote.KtorClient
import org.example.project.data.repository.MockLoginRepository
import org.example.project.data.repository.NetworkLoginRepository
import org.example.project.domain.repository.LoginRepository
import org.example.project.domain.usecase.LoginUseCase
import org.example.project.presentation.loginScreen.LoginViewModel
import org.koin.dsl.module

val appModule = module {
    // --- Network ---
    single { KtorClient.client }

    // --- Repositories ---
    single<LoginRepository> { MockLoginRepository() }
    //single<LoginRepository> { NetworkLoginRepository(get()) }

    // --- Use Cases ---
    factory { LoginUseCase(get()) }

    // --- ViewModels ---
    factory { LoginViewModel(get()) }
}
