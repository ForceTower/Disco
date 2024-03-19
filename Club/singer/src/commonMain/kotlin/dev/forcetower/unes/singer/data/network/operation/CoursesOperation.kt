package dev.forcetower.unes.singer.data.network.operation

import dev.forcetower.unes.singer.data.model.base.CourseDataDTO
import dev.forcetower.unes.singer.data.model.dto.Course
import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import dev.forcetower.unes.singer.domain.model.Authorization
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.takeFrom

class CoursesOperation(
    private val client: HttpClient
) : Operation {
    suspend fun execute(personId: Long, auth: Authorization): Course? {
        val url = "diario/solicitacao-aluno"
        val response = client.get {
            url {
                takeFrom(url)
                parameter("idAluno", personId)
            }
            header("Authorization", createAuth(auth))
        }.body<Items<CourseDataDTO>>()

        val item = response.items.firstOrNull() ?: return null
        return Course(
            item.id,
            item.name,
            item.resumed
        )
    }
}