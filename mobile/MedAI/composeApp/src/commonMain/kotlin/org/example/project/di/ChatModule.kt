package org.example.project.di

import org.example.project.data.repository.mock.MockChatRepository
import org.example.project.domain.repository.chat.ChatRepository
import org.example.project.domain.usecase.chat.*
import org.example.project.presentation.chatScreen.ChatListViewModel
import org.example.project.presentation.chatScreen.ChatViewModel
import org.example.project.presentation.doctor.chat.DoctorChatListViewModel
import org.koin.dsl.module

val chatModule = module {
    single<ChatRepository> { MockChatRepository() }

    factory { GetConversationsUseCase(get()) }
    factory { GetChatMessagesUseCase(get()) }
    factory { SendMessageUseCase(get()) }
    factory { ObserveTypingUseCase(get()) }
    factory { GetPatientConversationsUseCase(get()) }

    factory { ChatListViewModel(get()) }
    factory { DoctorChatListViewModel(get()) }
    factory { (doctorId: String, doctorName: String) ->
        ChatViewModel(doctorId, doctorName, get(), get(), get())
    }
}
