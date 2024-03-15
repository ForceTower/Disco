package dev.forcetower.unes.club.data.di

import dev.forcetower.unes.club.di.SharedModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(SharedModule.modules)
    }
}