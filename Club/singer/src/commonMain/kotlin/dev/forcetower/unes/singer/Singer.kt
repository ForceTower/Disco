package dev.forcetower.unes.singer

import dev.forcetower.unes.singer.data.model.dto.DisciplineData
import dev.forcetower.unes.singer.data.model.dto.Lecture
import dev.forcetower.unes.singer.data.model.dto.LectureMissed
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

    suspend fun grades(personId: Long, semesterId: Long, authorization: Authorization? = null): List<DisciplineData> {
        return api.grades(personId, semesterId, authorization ?: requireAuth())
    }

    suspend fun lectures(classId: Long, limit: Int = 0, offset: Int = 0, authorization: Authorization? = null): List<Lecture> {
        return api.lectures(classId, limit, offset, authorization ?: requireAuth())
    }

    suspend fun absences(personId: Long, classId: Long, limit: Int = 0, offset: Int = 0, authorization: Authorization? = null): List<LectureMissed> {
        return api.absences(personId, classId, limit, offset, authorization ?: requireAuth())
    }
}