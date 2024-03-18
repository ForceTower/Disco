package dev.forcetower.unes.club.data.service.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createBasicClient(): HttpClient {
    return HttpClient(Darwin) {
        BrowserUserAgent()
        install(ContentNegotiation) {
            json(
                Json(DefaultJson) {
                    ignoreUnknownKeys = true
                }
            )
        }
//            install(Logging) {
//                logger = Logger.DEFAULT
//                level = LogLevel.ALL
//            }
    }
}