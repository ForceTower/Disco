package dev.forcetower.unes.club.domain.di

import dev.forcetower.unes.club.domain.usecase.InsertAccessUseCase
import dev.forcetower.unes.club.domain.usecase.auth.ConnectedUserUseCase
import dev.forcetower.unes.club.domain.usecase.auth.LoginPortalUseCase
import dev.forcetower.unes.club.domain.usecase.disciplines.GetDisciplinesUseCase
import dev.forcetower.unes.club.domain.usecase.messages.GetAllMessagesUseCase
import org.koin.dsl.module

object DomainDI {
    val useCase = module {
        single { InsertAccessUseCase(get()) }
        factory { LoginPortalUseCase(get(), get(), get(), get()) }
        factory { ConnectedUserUseCase(get()) }
        factory { GetAllMessagesUseCase(get()) }
        factory { GetDisciplinesUseCase(get()) }
    }
}