package dev.forcetower.unes.singer.data.network

import dev.forcetower.unes.singer.data.model.base.MessageDTO
import dev.forcetower.unes.singer.data.model.dto.Message
import dev.forcetower.unes.singer.data.model.dto.MessageDiscipline
import dev.forcetower.unes.singer.data.model.dto.MessagesDataPage
import dev.forcetower.unes.singer.data.model.dto.Semester
import dev.forcetower.unes.singer.data.model.dto.Person
import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import dev.forcetower.unes.singer.data.model.dto.aggregators.ItemsTimed
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
    private fun createAuth(auth: Authorization): String {
        val usernameAndPassword = "${auth.username}:${auth.password}"
        val encoded = usernameAndPassword.encodeBase64()
        return "Basic $encoded"
    }

    suspend fun me(auth: Authorization): Person {
        return client.getWithAuth("eu", auth).body<Person>()
    }

    suspend fun messages(id: Long, auth: Authorization, until: String = "", amount: Int = 10): MessagesDataPage {
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

    suspend fun semesters(id: Long, auth: Authorization): List<Semester> {
        val url = "diario/periodos-letivos"
        val result =  client.get {
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

    private suspend fun HttpClient.getWithAuth(urlString: String, auth: Authorization, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
        return get {
            url(urlString)
            header("Authorization", createAuth(auth))
            block()
        }
    }
}