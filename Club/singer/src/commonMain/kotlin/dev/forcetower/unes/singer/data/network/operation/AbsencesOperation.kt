package dev.forcetower.unes.singer.data.network.operation

import dev.forcetower.unes.singer.data.model.base.MissedLectureDTO
import dev.forcetower.unes.singer.data.model.dto.Lecture
import dev.forcetower.unes.singer.data.model.dto.LectureMissed
import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import dev.forcetower.unes.singer.domain.model.Authorization
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.takeFrom

class AbsencesOperation(
    private val client: HttpClient
) : Operation {
    suspend fun execute(
        personId: Long,
        classId: Long,
        limit: Int,
        offset: Int,
        auth: Authorization
    ): List<LectureMissed> {
        val url = "diario/faltas"
        val response = client.get {
            url {
                takeFrom(url)
                parameter("idTurma", classId)
                parameter("idPessoa", personId)
                parameter("quantidade", limit)
                parameter("tokenPagina", offset)
                parameter("perfil", 1)
                parameter("campos", "proximaPagina,itens(id,abonada,retroativa,aula(id,ordinal,situacao,assunto,extra,data))")
                parameter("embutir", "itens(aula)")
            }
            header("Authorization", createAuth(auth))
        }.body<Items<MissedLectureDTO>>()

        return response.items.map {
            LectureMissed(
                it.id,
                it.accredited,
                it.retroactive,
                Lecture.fromDTO(it.lecture)
            )
        }
    }
}