package dev.forcetower.unes.singer.data.network.operation

import dev.forcetower.unes.singer.domain.model.Authorization
import io.ktor.util.encodeBase64

interface Operation {
    fun createAuth(auth: Authorization): String {
        val usernameAndPassword = "${auth.username}:${auth.password}"
        val encoded = usernameAndPassword.encodeBase64()
        return "Basic $encoded"
    }
}