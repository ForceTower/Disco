package dev.forcetower.unes.club.data.service.client

import dev.forcetower.unes.club.data.model.remote.edge.auth.ServiceAccessTokenDTO
import dev.forcetower.unes.club.domain.model.auth.PasskeyAssertionData
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

class AuthService(
    private val client: HttpClient,
    override val database: GeneralDatabase
) : UService {
    suspend fun anonymous(username: String, password: String, provider: String): HttpResponse {
        val endpoint = "auth/login/anonymous"
        val response = client.post {
            url { createUrl(endpoint) }
            withData(mapOf("username" to username, "password" to password, "provider" to provider))
        }

        return response
    }

    suspend fun passkeyAssertionStart(): PasskeyAssertionData {
        val endpoint = "auth/login/passkey/assertion/start"
        return client.get {
            url { createUrl(endpoint) }
        }.body<PasskeyAssertionData>()
    }

    suspend fun passkeyAssertionFinish(flowId: String, credential: String): HttpResponse {
        val endpoint = "auth/login/passkey/assertion/finish"
        return client.post {
            url { createUrl(endpoint) }
            withData(mapOf("flowId" to flowId, "credential" to credential))
        }
    }
}