package dev.forcetower.unes.singer

import dev.forcetower.unes.singer.data.model.dto.MessagesDataPage
import dev.forcetower.unes.singer.data.model.dto.Semester
import dev.forcetower.unes.singer.data.model.dto.Person
import dev.forcetower.unes.singer.data.network.SingerAPI
import dev.forcetower.unes.singer.domain.model.Authorization

class Singer internal constructor(
    private val api: SingerAPI
) {
    private var authorization: Authorization? = null

    fun setDefaultAuthorization(authorization: Authorization) {
        this.authorization = authorization
    }

    @Throws(IllegalStateException::class)
    private fun requireAuth(): Authorization {
        return authorization ?: throw IllegalStateException("Authentication Required")
    }

    suspend fun me(authorization: Authorization): Person {
        return api.me(authorization)
    }

    suspend fun messages(id: Long, authorization: Authorization? = null): MessagesDataPage {
        return api.messages(id, authorization ?: requireAuth())
    }

    suspend fun semesters(id: Long, authorization: Authorization? = null): List<Semester> {
        return api.semesters(id, authorization ?: requireAuth())
    }
}