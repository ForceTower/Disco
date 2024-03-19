package dev.forcetower.unes.singer.data.network

import dev.forcetower.unes.singer.data.model.dto.Course
import dev.forcetower.unes.singer.data.model.dto.DisciplineData
import dev.forcetower.unes.singer.data.model.dto.Lecture
import dev.forcetower.unes.singer.data.model.dto.LectureMissed
import dev.forcetower.unes.singer.data.model.dto.MessagesDataPage
import dev.forcetower.unes.singer.data.model.dto.Person
import dev.forcetower.unes.singer.data.model.dto.Semester
import dev.forcetower.unes.singer.data.network.operation.AbsencesOperation
import dev.forcetower.unes.singer.data.network.operation.CoursesOperation
import dev.forcetower.unes.singer.data.network.operation.GradesOperation
import dev.forcetower.unes.singer.data.network.operation.LecturesOperation
import dev.forcetower.unes.singer.data.network.operation.MessagesOperation
import dev.forcetower.unes.singer.data.network.operation.SemestersOperation
import dev.forcetower.unes.singer.domain.exception.InvalidLoginCredentialException
import dev.forcetower.unes.singer.domain.exception.LoginDevException
import dev.forcetower.unes.singer.domain.model.Authorization
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.util.encodeBase64

class SingerAPI(
    private val client: HttpClient
) {
    private val messages = MessagesOperation(client)
    private val semesters = SemestersOperation(client)
    private val grades = GradesOperation(client)
    private val lectures = LecturesOperation(client)
    private val absences = AbsencesOperation(client)
    private val courses = CoursesOperation(client)

    private fun createAuth(auth: Authorization): String {
        val usernameAndPassword = "${auth.username}:${auth.password}"
        val encoded = usernameAndPassword.encodeBase64()
        return "Basic $encoded"
    }

    suspend fun me(auth: Authorization): Person {
        val response = client.getWithAuth("eu", auth)
        when (response.status) {
            HttpStatusCode.OK -> {
                try {
                    return response.body<Person>()
                } catch (error: Throwable) {
                    throw LoginDevException(error)
                }
            }
            HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> throw InvalidLoginCredentialException()
            else -> throw IllegalStateException("Unknown error. Code ${response.status.value}")
        }
    }

    suspend fun messages(id: Long, auth: Authorization, until: String = "", amount: Int = 10): MessagesDataPage {
        return messages.execute(id, auth, until, amount)
    }

    suspend fun semesters(id: Long, auth: Authorization): List<Semester> {
        return semesters.execute(id, auth)
    }

    suspend fun grades(personId: Long, semesterId: Long, auth: Authorization): List<DisciplineData> {
        return grades.execute(personId, semesterId, auth)
    }

    suspend fun lectures(
        classId: Long,
        limit: Int,
        offset: Int,
        auth: Authorization
    ): List<Lecture> {
        return lectures.execute(classId, limit, offset, auth)
    }

    suspend fun absences(
        personId: Long,
        classId: Long,
        limit: Int,
        offset: Int,
        auth: Authorization
    ): List<LectureMissed> {
        return absences.execute(personId, classId, limit, offset, auth)
    }

    suspend fun course(personId: Long, auth: Authorization): Course? {
        return courses.execute(personId, auth)
    }

    private suspend fun HttpClient.getWithAuth(urlString: String, auth: Authorization, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
        return get {
            url(urlString)
            header("Authorization", createAuth(auth))
            block()
        }
    }
}