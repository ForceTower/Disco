package dev.forcetower.unes.singer.data.network.operation

import dev.forcetower.unes.singer.data.model.base.SemesterCompleteDTO
import dev.forcetower.unes.singer.data.model.dto.DisciplineData
import dev.forcetower.unes.singer.domain.model.Authorization
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.takeFrom

class GradesOperation(
    private val client: HttpClient
) : Operation {
    suspend fun execute(personId: Long, semesterId: Long, auth: Authorization): List<DisciplineData> {
        val url = "diario/periodos-letivos/${semesterId}"

        val response = client.get {
            url {
                takeFrom(url)
                parameter("idPessoa", personId)
                parameter("perfil", 1)
                parameter("campos", "id,codigo,descricao,turmas(itens(id,limiteFaltas,resultado(-%24link),classes(itens(atividadeCurricular(id,ementa,cargaHoraria),id,descricao,tipo,professores(itens(pessoa(id,nome,email,tipoPessoa))),alocacoes(itens(espacoFisico,horario)))),atividadeCurricular(id,nome,codigo,ementa,cargaHoraria,departamento(nome)),avaliacoes(itens(nome,nomeResumido,nota,avaliacoes(itens(nome,ordinal,nomeResumido,data,peso,nota(valor))))),periodoLetivo(codigo)))")
                parameter("embutir", "turmas(itens(resultado,classes(itens(atividadeCurricular,professores(itens(pessoa)),alocacoes(itens(espacoFisico,horario)))),atividadeCurricular(departamento(nome)),avaliacoes(itens(avaliacoes(itens(nota)))),periodoLetivo(codigo)))")
            }
            header("Authorization", createAuth(auth))
        }.body<SemesterCompleteDTO>()

        return response.disciplines.items.map {
            DisciplineData.createFromDTO(it)
        }
    }
}