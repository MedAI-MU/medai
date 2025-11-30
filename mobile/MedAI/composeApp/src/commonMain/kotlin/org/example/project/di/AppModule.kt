package org.example.project.di

import org.example.project.data.remote.KtorClient
import org.example.project.data.repository.MockHomeRepository
import org.example.project.data.repository.MockLoginRepository
import org.example.project.data.repository.NetworkLoginRepository
import org.example.project.domain.repository.HomeRepository
import org.example.project.domain.repository.LoginRepository
import org.example.project.domain.usecase.GetHomeDataUseCase
import org.example.project.domain.usecase.LoginUseCase
import org.example.project.presentation.homeScreen.HomeViewModel
import org.example.project.presentation.loginScreen.LoginViewModel
import org.koin.dsl.module

val appModule = module {
    // --- Network ---
    single { KtorClient.client }

    // --- Repositories ---
    single<LoginRepository> { MockLoginRepository() }
    single<HomeRepository> { MockHomeRepository() }
    //single<LoginRepository> { NetworkLoginRepository(get()) }
    //single<HomeRepository> { NetworkHomeRepository(get()) }

    // --- Use Cases ---
    factory { LoginUseCase(get()) }
    factory { GetHomeDataUseCase(get()) }

    // --- ViewModels ---
    factory { LoginViewModel(get()) }
    factory { HomeViewModel(get()) }
}
