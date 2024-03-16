package dev.forcetower.unes.singer.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.json.Json
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.math.roundToLong

actual class PlatformClientFactory : SingerClientFactory {
    override fun create(baseUrl: String, userAgent: String): HttpClient {
        return HttpClient(Darwin) {
            defaultRequest {
                url {
                    takeFrom(baseUrl)
                    parameters.appendIfNameAbsent("_", "${(NSDate().timeIntervalSince1970 * 1000).roundToLong()}")
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
                    Json {
                        encodeDefaults = true
                        isLenient = true
                        allowSpecialFloatingPointValues = true
                        allowStructuredMapKeys = true
                        prettyPrint = false
                        useArrayPolymorphism = false
                        ignoreUnknownKeys = true
                    }
                )
            }
//            install(Logging) {
//                logger = Logger.DEFAULT
//                level = LogLevel.INFO
//            }
        }
    }
}