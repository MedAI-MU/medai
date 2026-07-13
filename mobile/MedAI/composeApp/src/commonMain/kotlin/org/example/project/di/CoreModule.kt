package org.example.project.di

import org.example.project.core.domain.ResourceProvider
import org.example.project.core.presentation.util.CalendarManager
import org.example.project.core.presentation.util.ResourceProviderImpl
import org.example.project.data.remote.KtorClientFactory
import org.example.project.data.repository.InMemoryUserSessionManager
import org.example.project.domain.repository.auth.UserSessionManager
import org.koin.dsl.module

val coreModule = module {
    single { CalendarManager() }
    single<ResourceProvider> { ResourceProviderImpl() }
    single<UserSessionManager> { InMemoryUserSessionManager(get()) }
    single { org.example.project.data.remote.PersistentCookiesStorage(sessionManager = get()) }
    single { KtorClientFactory(sessionManager = get(), cookiesStorage = get()).create() }
}
