package dev.forcetower.unes.club.domain.repository.remote.uefs

import dev.forcetower.unes.club.domain.model.bigtray.BigTrayData

internal interface BigTrayRepository {
    suspend fun getQuota(): BigTrayData
}