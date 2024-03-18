package dev.forcetower.unes.club.data.repository.remote.uefs

import dev.forcetower.unes.club.domain.model.bigtray.BigTrayData
import dev.forcetower.unes.club.domain.repository.remote.uefs.BigTrayRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

internal class BigTrayRepositoryImpl(
    private val client: HttpClient
) : BigTrayRepository {
    override suspend fun getQuota(): BigTrayData {
        try {
            val response = client.get {
                url("http://www.propaae.uefs.br/ru/getCotas.php")
            }

            if (response.status != HttpStatusCode.OK) {
                return BigTrayData.error()
            }

            val value = response.bodyAsText()
            return if (value.equals("false", ignoreCase = true)) {
                BigTrayData.closed()
            } else {
                val values = value.split(";")
                if (values.size == 2) {
                    BigTrayData.createData(values)
                } else {
                    println("The size of the big tray has changed to ${values.size}")
                    BigTrayData.error()
                }
            }
        } catch (e: Exception) {
            return BigTrayData.error()
        }
    }
}