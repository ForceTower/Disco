package dev.forcetower.unes.club.data.di

import app.cash.sqldelight.db.SqlDriver
import dev.forcetower.unes.club.data.storage.database.GeneralDatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal actual val sharedModule = module {
    single<SqlDriver> { GeneralDatabaseDriverFactory(androidContext()).createDriver() }
}