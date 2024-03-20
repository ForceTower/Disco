package dev.forcetower.unes.club.data.service.client

import dev.forcetower.unes.club.data.model.remote.edge.ServiceAccountDTO
import dev.forcetower.unes.club.data.model.remote.edge.ServiceResponseWrapper
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class AccountService(
    private val client: HttpClient,
    override val database: GeneralDatabase
): UService {
    suspend fun me(): ServiceAccountDTO {
        val endpoint = "account/me"
        val result = client.get {
            url {
                createUrl(endpoint)
            }
            withAuth()
        }.body<ServiceResponseWrapper<ServiceAccountDTO>>()

        return result.data
    }
}