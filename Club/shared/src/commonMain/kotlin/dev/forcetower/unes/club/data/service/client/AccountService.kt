package dev.forcetower.unes.club.data.service.client

import dev.forcetower.unes.club.data.model.remote.edge.account.LinkEmailResponseDTO
import dev.forcetower.unes.club.data.model.remote.edge.account.ServiceAccountDTO
import dev.forcetower.unes.club.data.model.remote.edge.ServiceResponseWrapper
import dev.forcetower.unes.club.data.model.remote.edge.account.RegisterPasskeyCredential
import dev.forcetower.unes.club.data.model.remote.edge.account.RegisterPasskeyStart
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

    suspend fun registerPasskeyStart(): RegisterPasskeyStart {
        val endpoint = "passkeys/register/start"
        return client.get {
            url { createUrl(endpoint) }
            withAuth()
        }.body<RegisterPasskeyStart>()
    }

    suspend fun registerPasskeyFinish(flowId: String, data: String) {
        val endpoint = "passkeys/register/finish"
        val response = client.post {
            url { createUrl(endpoint) }
            withAuth()
            withData(RegisterPasskeyCredential(flowId, data))
        }

        if (response.status != HttpStatusCode.OK) {
            throw IllegalStateException("Failed with code ${response.status}")
        }
    }

    suspend fun registerMessagingToken(token: String) {
        val endpoint = "account/fcm"
        val response = client.post {
            url { createUrl(endpoint) }
            withAuth()
            withData(mapOf("token" to token))
        }

        if (response.status != HttpStatusCode.OK)
            throw IllegalStateException("Failed with code ${response.status}")
    }

    suspend fun changeProfilePicture(base64: String) {
        val endpoint = "account/picture"
        val response = client.post {
            url { createUrl(endpoint) }
            withAuth()
            withData(mapOf("picture" to base64))
        }

        if (response.status != HttpStatusCode.OK)
            throw IllegalStateException("Failed with code ${response.status}")
    }
}