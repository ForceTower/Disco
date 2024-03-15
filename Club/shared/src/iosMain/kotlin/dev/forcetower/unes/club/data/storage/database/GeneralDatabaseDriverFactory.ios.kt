package dev.forcetower.unes.club.data.storage.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

internal actual class GeneralDatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(GeneralDatabase.Schema, "general.db")
    }
}