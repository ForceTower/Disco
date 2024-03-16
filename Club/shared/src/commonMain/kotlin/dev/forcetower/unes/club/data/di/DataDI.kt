package dev.forcetower.unes.club.data.di

import dev.forcetower.unes.club.data.repository.local.AccessRepositoryImpl
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.GeneralDatabaseDriverFactory
import dev.forcetower.unes.club.domain.repository.local.AccessRepository
import dev.forcetower.unes.singer.Singer
import dev.forcetower.unes.singer.SingerFactory
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal object DataDI {
    val repository = module {
        single<AccessRepository> { AccessRepositoryImpl(get()) }
    }

    val data = module {
        single<GeneralDatabase> { GeneralDatabase(get()) }
        single<Singer> { SingerFactory().create(get(named("platform-user-agent"))) }
    }
}