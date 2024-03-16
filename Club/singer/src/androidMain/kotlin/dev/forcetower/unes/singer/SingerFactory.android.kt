package dev.forcetower.unes.singer

import dev.forcetower.unes.singer.data.network.PlatformClientFactory
import io.ktor.client.HttpClient

actual fun getSingerClient(
    baseUrl: String,
    agent: String
): HttpClient {
    return PlatformClientFactory().create(baseUrl, agent)
}