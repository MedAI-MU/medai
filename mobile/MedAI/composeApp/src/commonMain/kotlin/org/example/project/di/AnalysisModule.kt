package org.example.project.di

import org.example.project.data.repository.AnalysisRepositoryImpl
import org.example.project.domain.repository.AnalysisRepository
import org.example.project.domain.usecase.AnalyzeAudioUseCase
import org.example.project.presentation.analysis.AnalysisViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analysisModule = module {
    singleOf(::AnalysisRepositoryImpl) bind AnalysisRepository::class
    singleOf(::AnalyzeAudioUseCase)
    factory { AnalysisViewModel(get(), get()) }
}
