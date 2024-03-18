package dev.forcetower.unes.club.data.service.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createBasicClient(): HttpClient {
    return HttpClient(OkHttp) {
        BrowserUserAgent()
        install(ContentNegotiation) {
            json(
                Json(DefaultJson) {
                    ignoreUnknownKeys = true
                }
            )
        }
    }
}