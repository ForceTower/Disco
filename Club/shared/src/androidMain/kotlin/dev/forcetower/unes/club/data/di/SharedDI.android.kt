package dev.forcetower.unes.club.data.di

import app.cash.sqldelight.db.SqlDriver
import dev.forcetower.unes.club.data.storage.database.GeneralDatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual val sharedModule = module {
    single<SqlDriver> { GeneralDatabaseDriverFactory(androidContext()).createDriver() }
    single(named("platform-user-agent")) {
        """
           Mozilla/5.0 (iPhone; CPU iPhone OS 16_5_1 like Mac OS X) 
           AppleWebKit/605.1.15 (KHTML, like Gecko) 
           Version/16.5.2 (a) Mobile/15E148 Safari/604.1 
        """.trimIndent()
    }
}