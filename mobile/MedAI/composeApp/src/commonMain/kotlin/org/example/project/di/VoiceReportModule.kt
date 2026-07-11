package org.example.project.di

import org.example.project.data.repository.NetworkVoiceReportRepository
import org.example.project.domain.repository.voice_report.VoiceReportRepository
import org.example.project.domain.usecase.voice_report.*
import org.example.project.presentation.appointmentScreen.voiceReport.VoiceReportViewModel
import org.koin.dsl.module

val voiceReportModule = module {
    single<VoiceReportRepository> { NetworkVoiceReportRepository(get()) }

    factory { UploadVoiceReportUseCase(get()) }
    factory { GetVoiceReportsUseCase(get()) }
    factory { GetVoiceReportStatusUseCase(get()) }
    factory { DeleteVoiceReportUseCase(get()) }
    factory { PollVoiceReportStatusUseCase(get()) }

    factory { (appointmentId: String) ->
        VoiceReportViewModel(
            appointmentId = appointmentId,
            audioRecorder = get(),
            permissionHandler = get(),
            uploadVoiceReportUseCase = get(),
            getVoiceReportsUseCase = get(),
            getVoiceReportStatusUseCase = get(),
            deleteVoiceReportUseCase = get(),
            pollVoiceReportStatusUseCase = get()
        )
    }
}
