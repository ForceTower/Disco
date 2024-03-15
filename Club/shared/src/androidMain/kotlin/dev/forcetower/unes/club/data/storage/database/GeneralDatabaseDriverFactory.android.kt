package dev.forcetower.unes.club.data.storage.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

internal actual class GeneralDatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(GeneralDatabase.Schema, context, "general.db")
    }
}