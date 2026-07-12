package org.example.project.di

import org.example.project.data.remote.api.ReportAnalysisApiService
import org.example.project.data.remote.api.KtorReportAnalysisApiService
import org.example.project.data.repository.NetworkReportAnalysisRepository
import org.example.project.domain.repository.report_analysis.ReportAnalysisRepository
import org.example.project.domain.usecase.report_analysis.*
import org.example.project.presentation.reportAnalysis.ReportAnalysisViewModel
import org.koin.dsl.module

val reportAnalysisModule = module {
    single<ReportAnalysisApiService> { KtorReportAnalysisApiService(get()) }
    single<ReportAnalysisRepository> { NetworkReportAnalysisRepository(get()) }

    // Use Cases
    factory { UploadReportUseCase(get()) }
    factory { TriggerAnalysisUseCase(get()) }
    factory { PollAnalysisStatusUseCase(get()) }
    factory { GetAllReportsUseCase(get()) }
    factory { GetAnalysisUseCase(get()) }

    // ViewModel
    factory { params ->
        val patientId = params.getOrNull<String>()
        ReportAnalysisViewModel(
            patientIdArg = patientId,
            sessionManager = get(),
            appointmentRepository = get(),
            uploadReportUseCase = get(),
            triggerAnalysisUseCase = get(),
            pollAnalysisStatusUseCase = get(),
            getAllReportsUseCase = get(),
            getAnalysisUseCase = get()
        )
    }
}
