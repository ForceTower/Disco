package dev.forcetower.unes.club.domain.di

import dev.forcetower.unes.club.domain.usecase.InsertAccessUseCase
import dev.forcetower.unes.club.domain.usecase.auth.LoginPortalUseCase
import org.koin.dsl.module

object DomainDI {
    val useCase = module {
        single { InsertAccessUseCase(get()) }
        factory { LoginPortalUseCase(get(), get(), get()) }
    }
}