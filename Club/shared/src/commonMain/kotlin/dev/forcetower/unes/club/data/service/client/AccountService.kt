package dev.forcetower.unes.club.data.service.client

import dev.forcetower.unes.club.data.model.remote.edge.account.LinkEmailResponseDTO
import dev.forcetower.unes.club.data.model.remote.edge.account.ServiceAccountDTO
import dev.forcetower.unes.club.data.model.remote.edge.ServiceResponseWrapper
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.model.auth.ServiceLinkEmailCompleteResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode

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

    suspend fun linkEmail(email: String): String {
        val endpoint = "account/register/start"
        val result = client.post {
            url { createUrl(endpoint) }
            withAuth()
            withData(mapOf("email" to email))
        }
        if (result.status != HttpStatusCode.OK)
            throw IllegalStateException("Request failed with code ${result.status.value}")

        return result.body<ServiceResponseWrapper<LinkEmailResponseDTO>>().data.securityToken
    }

    suspend fun completeLinkEmail(code: String, security: String): ServiceLinkEmailCompleteResult {
        val endpoint = "account/register/complete"
        val result = client.post {
            url { createUrl(endpoint) }
            withAuth()
            withData(mapOf("code" to code, "securityToken" to security))
        }

        if (result.status == HttpStatusCode.OK) return ServiceLinkEmailCompleteResult.Success
        if (result.status == HttpStatusCode.BadRequest) return ServiceLinkEmailCompleteResult.InvalidCode
        return ServiceLinkEmailCompleteResult.Error(result.status.value, "Request failed with code ${result.status.value}")
    }
}