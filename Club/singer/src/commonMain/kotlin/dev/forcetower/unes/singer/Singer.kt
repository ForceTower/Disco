package dev.forcetower.unes.singer

import dev.forcetower.unes.singer.data.model.dto.Semester
import dev.forcetower.unes.singer.data.model.dto.Person
import dev.forcetower.unes.singer.data.network.SingerAPI
import dev.forcetower.unes.singer.domain.model.SingerAuthorization

class Singer internal constructor(
    private val api: SingerAPI
) {
    private var authorization: SingerAuthorization? = null

    fun setDefaultAuthorization(authorization: SingerAuthorization) {
        this.authorization = authorization
    }

    @Throws(IllegalStateException::class)
    private fun requireAuth(): SingerAuthorization {
        return authorization ?: throw IllegalStateException("Authentication Required")
    }

    suspend fun me(authorization: SingerAuthorization): Person {
        return api.me(authorization)
    }

    suspend fun semesters(id: Long, authorization: SingerAuthorization? = null): List<Semester> {
        return api.semesters(id, authorization ?: requireAuth())
    }
}