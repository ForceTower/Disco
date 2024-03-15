package dev.forcetower.unes.club.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(SharedModule.modules)
    }.koin
}