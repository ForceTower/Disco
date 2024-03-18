package dev.forcetower.unes.singer.data.network.operation

import dev.forcetower.unes.singer.data.model.base.LectureDTO
import dev.forcetower.unes.singer.data.model.base.SemesterCompleteDTO
import dev.forcetower.unes.singer.data.model.dto.Lecture
import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import dev.forcetower.unes.singer.domain.model.Authorization
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.takeFrom

class LecturesOperation(
    private val client: HttpClient
) : Operation {
    suspend fun execute(
        classId: Long,
        limit: Int,
        offset: Int,
        auth: Authorization
    ): List<Lecture> {
        val url = "diario/aulas"

        val response = client.get {
            url {
                takeFrom(url)
                parameter("idClasse", classId)
                parameter("quantidade", limit)
                parameter("tokenPagina", offset)
                parameter("campos", "proximaPagina,itens(planoAula,ordinal,data,situacao,assunto,materiaisApoio,tarefa)")
                parameter("embutir", "itens(materiaisApoio)")
            }
            header("Authorization", createAuth(auth))
        }.body<Items<LectureDTO>>()

        return response.items.map { Lecture.fromDTO(it) }
    }
}