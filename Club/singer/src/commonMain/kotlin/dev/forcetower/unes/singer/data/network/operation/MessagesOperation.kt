package dev.forcetower.unes.singer.data.network.operation

import dev.forcetower.unes.singer.data.model.base.MessageDTO
import dev.forcetower.unes.singer.data.model.dto.Message
import dev.forcetower.unes.singer.data.model.dto.MessageDiscipline
import dev.forcetower.unes.singer.data.model.dto.MessagesDataPage
import dev.forcetower.unes.singer.data.model.dto.aggregators.ItemsTimed
import dev.forcetower.unes.singer.domain.model.Authorization
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.takeFrom

class MessagesOperation(
    private val client: HttpClient
) : Operation {
    suspend fun execute(id: Long, auth: Authorization, until: String = "", amount: Int = 10): MessagesDataPage {
        val url = "diario/recados"
        val messages = client.get{
            url {
                takeFrom(url)
                parameter("idPessoa", id)
                parameter("ate", until)
                parameter("quantidade", amount)
                parameter("perfil", 1)
                parameter("campos", "itens(id,descricao,timeStamp,remetente(nome),perfilRemetente,escopos(itens(id,tipo,classe(id,descricao,tipo,atividadeCurricular(id,codigo,nome,nomeResumido,ementa,cargaHoraria,departamento(nome)))))),maisAntigos")
                parameter("embutir", "itens(remetente,escopos(itens(classe(atividadeCurricular(departamento)))))")
            }
            header("Authorization", createAuth(auth))
        }.body<ItemsTimed<MessageDTO>>()

        val mapped = messages.items.map {
            val clazz = it.scopes.items.firstOrNull()?.clazz
            val discipline = clazz?.let { element ->
                MessageDiscipline(
                    element.discipline.id,
                    element.id,
                    element.discipline.code,
                    element.discipline.name,
                    element.type
                )
            }
            Message(
                it.id,
                it.message,
                it.sender.name,
                it.timestamp,
                it.profileType,
                discipline
            )
        }

        val regex = Regex("ate=(\\d+-\\d+)")
        val next = messages.nextPage?.link?.href?.let { ref ->
            val groups = regex.find(ref)?.groups
            val size = groups?.size ?: 0
            if (size > 1) {
                groups?.get(1)?.value
            } else {
                null
            }
        }
        return MessagesDataPage(mapped, next)
    }
}