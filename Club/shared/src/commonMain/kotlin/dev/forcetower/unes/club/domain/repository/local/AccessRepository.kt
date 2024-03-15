package dev.forcetower.unes.club.domain.repository.local

import dev.forcetower.unes.club.data.storage.database.Access

interface AccessRepository {
    suspend fun insert(username: String, password: String)
    suspend fun requireCurrentAccess(): Access?
}