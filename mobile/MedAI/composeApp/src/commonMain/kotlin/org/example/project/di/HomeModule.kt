package org.example.project.di

import org.example.project.data.repository.NetworkHomeRepository
import org.example.project.domain.repository.home.HomeRepository
import org.example.project.domain.usecase.home.GetHomeDataUseCase
import org.example.project.presentation.homeScreen.HomeViewModel
import org.koin.dsl.module

val homeModule = module {
    single<HomeRepository> { NetworkHomeRepository(get()) }
    factory { GetHomeDataUseCase(get(), get()) }
    factory { HomeViewModel(get(), get(), get()) }
}
