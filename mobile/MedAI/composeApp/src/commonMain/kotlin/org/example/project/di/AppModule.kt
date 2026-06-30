package org.example.project.di

import org.example.project.data.repository.NetworkProfileRepository
import org.example.project.data.repository.mock.MockNotificationRepository
import org.example.project.domain.repository.notification.NotificationRepository
import org.example.project.domain.repository.profile.ProfileRepository
import org.example.project.domain.usecase.notification.GetNotificationsUseCase
import org.example.project.presentation.notificationScreen.NotificationViewModel
import org.example.project.presentation.profileScreen.ProfileViewModel
import org.koin.dsl.module

val appModule = module {
    includes(
        coreModule,
        authModule,
        homeModule,
        appointmentModule,
        chatModule,
        medicalRecordModule,
        doctorModule,
        secretaryModule,
        scheduleModule
    )

    // Other shared dependencies
    single<NotificationRepository> { MockNotificationRepository() }
    single<ProfileRepository> { NetworkProfileRepository(get()) }

    factory { GetNotificationsUseCase(get()) }

    factory { NotificationViewModel(get()) }
    factory { ProfileViewModel(get()) }
}
