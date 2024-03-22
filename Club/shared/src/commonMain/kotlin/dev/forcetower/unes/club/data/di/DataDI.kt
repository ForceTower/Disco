package dev.forcetower.unes.club.data.di

import dev.forcetower.unes.club.data.repository.local.AccessRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.ClassRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.DisciplineRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.GradeRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.MessageRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.ScheduleRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.SemesterRepositoryImpl
import dev.forcetower.unes.club.data.repository.local.SyncRepositoryImpl
import dev.forcetower.unes.club.data.repository.remote.edge.AccountRepositoryImpl
import dev.forcetower.unes.club.data.repository.remote.edge.AuthRepositoryImpl
import dev.forcetower.unes.club.data.repository.remote.uefs.BigTrayRepositoryImpl
import dev.forcetower.unes.club.data.service.client.AccountService
import dev.forcetower.unes.club.data.service.client.AuthService
import dev.forcetower.unes.club.data.service.client.ImgurService
import dev.forcetower.unes.club.data.service.client.SyncService
import dev.forcetower.unes.club.data.service.client.createBasicClient
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.repository.local.AccessRepository
import dev.forcetower.unes.club.domain.repository.local.ClassRepository
import dev.forcetower.unes.club.domain.repository.local.DisciplineRepository
import dev.forcetower.unes.club.domain.repository.local.GradeRepository
import dev.forcetower.unes.club.domain.repository.local.MessageRepository
import dev.forcetower.unes.club.domain.repository.local.ScheduleRepository
import dev.forcetower.unes.club.domain.repository.local.SemesterRepository
import dev.forcetower.unes.club.domain.repository.local.SyncRepository
import dev.forcetower.unes.club.domain.repository.remote.edge.AccountRepository
import dev.forcetower.unes.club.domain.repository.remote.edge.AuthRepository
import dev.forcetower.unes.club.domain.repository.remote.uefs.BigTrayRepository
import dev.forcetower.unes.singer.Singer
import dev.forcetower.unes.singer.SingerFactory
import io.ktor.client.HttpClient
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal object DataDI {
    val repository = module {
        single<AccessRepository> { AccessRepositoryImpl(get()) }
        single<MessageRepository> { MessageRepositoryImpl(get()) }
        single<DisciplineRepository> { DisciplineRepositoryImpl(get(), get(), get()) }
        single<ScheduleRepository> { ScheduleRepositoryImpl(get()) }
        single<ClassRepository> { ClassRepositoryImpl(get(), get(), get()) }
        single<SyncRepository> { SyncRepositoryImpl(get(), get(), get(), get()) }
        single<GradeRepository> { GradeRepositoryImpl(get()) }
        single<BigTrayRepository> { BigTrayRepositoryImpl(get()) }
        single<SemesterRepository> { SemesterRepositoryImpl(get()) }
        single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
        single<AccountRepository> { AccountRepositoryImpl(get(), get(), get(), get()) }
    }

    val data = module {
        single<GeneralDatabase> { GeneralDatabase(get()) }
        single<GeneralDB> { GeneralDB(get()) }
        single<HttpClient> { createBasicClient() }
        single<Singer> { SingerFactory().create(get(named("platform-user-agent"))) }
        single<Json> {
            Json(DefaultJson) {
                ignoreUnknownKeys = true
            }
        }
    }

    val service = module {
        single { AuthService(get(), get()) }
        single { AccountService(get(), get()) }
        single { SyncService(get(), get()) }
        single { ImgurService(get()) }
    }
}