package org.example.project.di

import org.example.project.data.repository.NetworkLoginRepository
import org.example.project.data.repository.NetworkSignUpRepository
import org.example.project.domain.repository.auth.LoginRepository
import org.example.project.domain.repository.auth.SignUpRepository
import org.example.project.domain.usecase.auth.LoginUseCase
import org.example.project.domain.usecase.auth.SignUpUseCase
import org.example.project.domain.usecase.auth.CheckAccountStatusUseCase
import org.example.project.presentation.loginScreen.LoginViewModel
import org.example.project.presentation.signUpScreen.SignUpViewModel
import org.example.project.presentation.pendingApprovalScreen.PendingApprovalViewModel
import org.koin.dsl.module

val authModule = module {
    single<LoginRepository> { NetworkLoginRepository(get()) }
    single<SignUpRepository> { NetworkSignUpRepository(client = get()) }

    factory { LoginUseCase(get(), get()) }
    factory { SignUpUseCase(repository = get(), sessionManager = get()) }
    factory { CheckAccountStatusUseCase(loginRepository = get(), sessionManager = get()) }

    factory { LoginViewModel(get()) }
    factory { SignUpViewModel(signUpUseCase = get()) }
    factory { PendingApprovalViewModel(checkAccountStatusUseCase = get(), sessionManager = get()) }
}
