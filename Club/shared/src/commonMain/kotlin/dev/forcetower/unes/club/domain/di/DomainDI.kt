package dev.forcetower.unes.club.domain.di

import dev.forcetower.unes.club.domain.usecase.InsertAccessUseCase
import org.koin.dsl.module

object DomainDI {
    val useCase = module {
        single { InsertAccessUseCase(get(), get()) }
    }
}