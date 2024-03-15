package dev.forcetower.unes.club.di

import dev.forcetower.unes.club.data.di.DataDI
import dev.forcetower.unes.club.data.di.sharedModule
import dev.forcetower.unes.club.domain.di.DomainDI

internal object SharedModule {
    val modules = listOf(
        DataDI.data,
        DataDI.repository,
        DomainDI.useCase,
        sharedModule
    )
}