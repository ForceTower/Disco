package dev.forcetower.unes.club.domain.repository.local

import dev.forcetower.unes.club.data.storage.database.Access
import dev.forcetower.unes.club.data.storage.database.Profile
import kotlinx.coroutines.flow.Flow

internal interface AccessRepository {
    suspend fun insert(username: String, password: String)
    suspend fun requireCurrentAccess(): Access?
    fun access(): Flow<Access?>
    fun currentProfile(): Flow<Profile?>

    suspend fun logout()
}