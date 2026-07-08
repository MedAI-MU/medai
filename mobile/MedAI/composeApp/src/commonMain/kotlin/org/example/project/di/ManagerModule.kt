package org.example.project.di

import org.example.project.data.repository.NetworkManagerRepository
import org.example.project.domain.repository.manager.ManagerRepository
import org.example.project.domain.usecase.manager.*
import org.example.project.presentation.manager.dashboard.ManagerDashboardViewModel
import org.koin.dsl.module

val managerModule = module {
    single<ManagerRepository> { NetworkManagerRepository(get()) }

    single { GetPendingDoctorsUseCase(get()) }
    single { GetPendingSecretariesUseCase(get()) }
    single { GetApprovedDoctorsUseCase(get()) }
    single { GetApprovedSecretariesUseCase(get()) }
    single { ApproveDoctorUseCase(get()) }
    single { ApproveSecretaryUseCase(get()) }
    single { RemoveUserUseCase(get()) }

    factory { ManagerDashboardViewModel(get(), get(), get(), get(), get(), get(), get()) }
}
