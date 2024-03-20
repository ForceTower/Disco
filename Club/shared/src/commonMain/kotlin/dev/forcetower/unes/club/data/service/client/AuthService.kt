package dev.forcetower.unes.club.data.service.client

import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthService(
    private val client: HttpClient,
    override val database: GeneralDatabase
) : UService {
    suspend fun anonymous(username: String, password: String, provider: String): HttpResponse {
        val endpoint = "auth/login/anonymous"
        val response = client.post {
            url { createUrl(endpoint) }
            contentType(ContentType.Application.Json)
            withData(mapOf("username" to username, "password" to password, "provider" to provider))
        }

        return response
    }
}