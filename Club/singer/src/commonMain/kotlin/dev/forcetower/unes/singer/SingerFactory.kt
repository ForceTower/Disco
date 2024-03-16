package dev.forcetower.unes.singer

import dev.forcetower.unes.singer.data.network.SingerAPI
import io.ktor.client.HttpClient

class SingerFactory {
    fun create(agent: String): Singer {
        val client = getSingerClient("http://academico.uefs.br/Api/SagresApi/", agent)
        val api = SingerAPI(client)
        return Singer(api)
    }
}

expect fun getSingerClient(baseUrl: String, agent: String): HttpClient