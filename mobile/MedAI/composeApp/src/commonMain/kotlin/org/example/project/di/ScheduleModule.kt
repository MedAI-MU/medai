package org.example.project.di

import org.example.project.data.repository.NetworkScheduleRepository
import org.example.project.domain.repository.schedule.ScheduleRepository
import org.example.project.domain.usecase.schedule.*
import org.example.project.presentation.schedule.ScheduleViewModel
import org.koin.dsl.module

val scheduleModule = module {
    single<ScheduleRepository> { NetworkScheduleRepository(client = get()) }

    factory { GetScheduleTemplatesUseCase(get()) }
    factory { CreateScheduleTemplateUseCase(get()) }
    factory { UpdateScheduleTemplateUseCase(get()) }
    factory { DeleteScheduleTemplateUseCase(get()) }
    factory { ApplyScheduleTemplateUseCase(get()) }
    factory { GetScheduleSlotsUseCase(get()) }
    factory { CreateScheduleSlotsUseCase(get()) }
    factory { UpdateScheduleSlotUseCase(get()) }
    factory { DeleteScheduleSlotUseCase(get()) }

    factory { (doctorId: Int) ->
        ScheduleViewModel(
            get(), get(), get(), get(), get(), get(), get(), get(), get(), doctorId
        )
    }
}
