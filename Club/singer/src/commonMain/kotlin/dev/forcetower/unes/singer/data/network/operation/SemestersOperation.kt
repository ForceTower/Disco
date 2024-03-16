package dev.forcetower.unes.singer.data.network.operation

import dev.forcetower.unes.singer.data.model.dto.Semester
import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import dev.forcetower.unes.singer.domain.model.Authorization
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.takeFrom

class SemestersOperation(
    private val client: HttpClient
) : Operation {
    suspend fun execute(id: Long, auth: Authorization): List<Semester> {
        val url = "diario/periodos-letivos"
        val result = client.get {
            url {
                takeFrom(url)
                parameter("idPessoa", id)
                parameter("perfil", 1)
                parameter("campos", "itens(id,codigo,descricao,inicio,fim)")
                parameter("quantidade", 0)
            }
            header("Authorization", createAuth(auth))
        }.body<Items<Semester>>()
        return result.items
    }
}