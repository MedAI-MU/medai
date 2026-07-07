package org.example.project.di

import org.example.project.data.remote.api.DiagnosisApiService
import org.example.project.data.remote.api.KtorDiagnosisApiService
import org.example.project.data.repository.NetworkDiagnosisRepository
import org.example.project.domain.repository.diagnosis.DiagnosisRepository
import org.example.project.domain.usecase.diagnosis.*
import org.example.project.presentation.shared.diagnosis.DiagnosisViewModel
import org.koin.dsl.module

val diagnosisModule = module {
    single<DiagnosisApiService> { KtorDiagnosisApiService(get()) }
    single<DiagnosisRepository> { NetworkDiagnosisRepository(get()) }

    // Use cases
    factory { GetPatientDiagnosesUseCase(get()) }
    factory { CreateDiagnosisUseCase(get()) }
    factory { UpdateDiagnosisUseCase(get()) }
    factory { DeleteDiagnosisUseCase(get()) }

    // ViewModel
    factory { params ->
        val patientId = params.getOrNull<String>()
        DiagnosisViewModel(
            patientIdArg = patientId,
            getPatientDiagnosesUseCase = get(),
            createDiagnosisUseCase = get(),
            updateDiagnosisUseCase = get(),
            deleteDiagnosisUseCase = get(),
            sessionManager = get(),
            appointmentRepository = get()
        )
    }
}
