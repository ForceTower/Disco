package dev.forcetower.unes.club.data.service.client

import dev.forcetower.unes.club.data.model.remote.edge.ServiceResponseWrapper
import dev.forcetower.unes.club.data.model.remote.edge.sync.PublicSyncRegistry
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class SyncService(
    private val client: HttpClient,
    override val database: GeneralDatabase
) : UService {
    suspend fun history(): List<PublicSyncRegistry> {
        val endpoint = "sync/history"
        return client.get {
            url { createUrl(endpoint) }
            withAuth()
        }.body<ServiceResponseWrapper<List<PublicSyncRegistry>>>().data
    }
}