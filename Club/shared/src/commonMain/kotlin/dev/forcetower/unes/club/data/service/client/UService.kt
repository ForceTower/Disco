package dev.forcetower.unes.club.data.service.client

import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.exception.ServiceUnauthenticatedException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.takeFrom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface UService {
    val database: GeneralDatabase

    fun URLBuilder.createUrl(endpoint: String): URLBuilder {
        return takeFrom("https://edge-unes.forcetower.dev/api/${endpoint}")
    }

    suspend fun HttpRequestBuilder.withAuth() {
        val token = withContext(Dispatchers.IO) {
            database.serviceAccessTokenQueries.selectToken().executeAsOneOrNull()
        } ?: throw ServiceUnauthenticatedException()
        headers.append(HttpHeaders.Authorization, "Bearer ${token.token}")
    }

    suspend fun requireAuthToken(): String {
        val token = withContext(Dispatchers.IO) {
            database.serviceAccessTokenQueries.selectToken().executeAsOneOrNull()
        } ?: throw ServiceUnauthenticatedException()
        return token.token
    }
}

inline fun <reified T> HttpRequestBuilder.withData(body: T) {
    contentType(ContentType.Application.Json)
    setBody(body)
}