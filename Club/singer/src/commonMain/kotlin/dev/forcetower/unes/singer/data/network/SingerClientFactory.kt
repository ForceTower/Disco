package dev.forcetower.unes.singer.data.network

import io.ktor.client.HttpClient

interface SingerClientFactory {
    fun create(baseUrl: String, userAgent: String): HttpClient
}

expect class PlatformClientFactory : SingerClientFactory

