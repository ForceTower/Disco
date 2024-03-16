package dev.forcetower.unes.singer.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.json.Json

actual class PlatformClientFactory : SingerClientFactory {
    override fun create(baseUrl: String, userAgent: String): HttpClient {
        return HttpClient(OkHttp) {
            defaultRequest {
                url {
                    takeFrom(baseUrl)
                    parameters.appendIfNameAbsent("_", "${System.currentTimeMillis()}")
                }
                headers
                    .appendIfNameAbsent("User-Agent", userAgent)
                    .appendIfNameAbsent("X-Requested-With", "com.tecnotrends.sagres.mobile")
                    .appendIfNameAbsent("Accept", "application/json, text/javascript, */*; q=0.01")
                    .appendIfNameAbsent("Accept-Encoding", "gzip, deflate")
                    .appendIfNameAbsent("Accept-Language", "en-US,en;q=0.9")
            }
            install(UserAgent) {
                agent = userAgent
            }
            install(ContentNegotiation) {
                json(
                    Json(DefaultJson) {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
}