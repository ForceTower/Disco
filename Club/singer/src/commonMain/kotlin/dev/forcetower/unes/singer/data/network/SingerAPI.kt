package dev.forcetower.unes.singer.data.network

import dev.forcetower.unes.singer.data.model.base.MessageDTO
import dev.forcetower.unes.singer.data.model.dto.DisciplineData
import dev.forcetower.unes.singer.data.model.dto.Message
import dev.forcetower.unes.singer.data.model.dto.MessageDiscipline
import dev.forcetower.unes.singer.data.model.dto.MessagesDataPage
import dev.forcetower.unes.singer.data.model.dto.Semester
import dev.forcetower.unes.singer.data.model.dto.Person
import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import dev.forcetower.unes.singer.data.model.dto.aggregators.ItemsTimed
import dev.forcetower.unes.singer.data.network.operation.GradesOperation
import dev.forcetower.unes.singer.data.network.operation.MessagesOperation
import dev.forcetower.unes.singer.data.network.operation.SemestersOperation
import dev.forcetower.unes.singer.domain.model.Authorization
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.takeFrom
import io.ktor.util.encodeBase64

class SingerAPI(
    private val client: HttpClient
) {
    private val messages = MessagesOperation(client)
    private val semesters = SemestersOperation(client)
    private val grades = GradesOperation(client)

    private fun createAuth(auth: Authorization): String {
        val usernameAndPassword = "${auth.username}:${auth.password}"
        val encoded = usernameAndPassword.encodeBase64()
        return "Basic $encoded"
    }

    suspend fun me(auth: Authorization): Person {
        return client.getWithAuth("eu", auth).body<Person>()
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

    private suspend fun HttpClient.getWithAuth(urlString: String, auth: Authorization, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
        return get {
            url(urlString)
            header("Authorization", createAuth(auth))
            block()
        }
    }
}