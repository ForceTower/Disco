package dev.forcetower.unes.club.data.di

import dev.forcetower.unes.club.data.repository.local.AccessRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.ClassRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.DisciplineRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.GradeRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.MessageRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.ScheduleRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.SyncRepositoryImpl
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.repository.local.AccessRepository
import dev.forcetower.unes.club.domain.repository.local.ClassRepository
import dev.forcetower.unes.club.domain.repository.local.DisciplineRepository
import dev.forcetower.unes.club.domain.repository.local.GradeRepository
import dev.forcetower.unes.club.domain.repository.local.MessageRepository
import dev.forcetower.unes.club.domain.repository.local.ScheduleRepository
import dev.forcetower.unes.club.domain.repository.local.SyncRepository
import dev.forcetower.unes.singer.Singer
import dev.forcetower.unes.singer.SingerFactory
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal object DataDI {
    val repository = module {
        single<AccessRepository> { AccessRepositoryImpl(get()) }
        single<MessageRepository> { MessageRepositoryImpl(get()) }
        single<DisciplineRepository> { DisciplineRepositoryImpl(get(), get(), get()) }
        single<ScheduleRepository> { ScheduleRepositoryImpl(get()) }
        single<ClassRepository> { ClassRepositoryImpl(get(), get(), get()) }
        single<SyncRepository> { SyncRepositoryImpl(get(), get(), get()) }
        single<GradeRepository> { GradeRepositoryImpl(get()) }
    }

    val data = module {
        single<GeneralDatabase> { GeneralDatabase(get()) }
        single<GeneralDB> { GeneralDB(get()) }
        single<Singer> { SingerFactory().create(get(named("platform-user-agent"))) }
    }
}