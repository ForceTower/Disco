package dev.forcetower.unes.singer.data.network

import dev.forcetower.unes.singer.data.model.dto.Semester
import dev.forcetower.unes.singer.data.model.dto.Person
import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import dev.forcetower.unes.singer.domain.model.SingerAuthorization
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
    private fun createAuth(auth: SingerAuthorization): String {
        val usernameAndPassword = "${auth.username}:${auth.password}"
        val encoded = usernameAndPassword.encodeBase64()
        return "Basic $encoded"
    }

    suspend fun me(auth: SingerAuthorization): Person {
        return client.getWithAuth("eu", auth).body<Person>()
    }

    suspend fun semesters(id: Long, auth: SingerAuthorization): List<Semester> {
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

    private suspend fun HttpClient.getWithAuth(urlString: String, auth: SingerAuthorization, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
        return get {
            url(urlString)
            header("Authorization", createAuth(auth))
            block()
        }
    }
}