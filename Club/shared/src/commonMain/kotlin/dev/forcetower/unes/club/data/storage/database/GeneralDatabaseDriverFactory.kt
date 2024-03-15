package dev.forcetower.unes.club.data.storage.database

import app.cash.sqldelight.db.SqlDriver

internal expect class GeneralDatabaseDriverFactory {
   fun createDriver(): SqlDriver
}
