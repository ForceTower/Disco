package dev.forcetower.unes.club.data.service.client

import dev.forcetower.unes.club.data.model.remote.imgur.ImgurUpload
import dev.forcetower.unes.club.data.model.remote.imgur.UploadResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.http.parameters

class ImgurService(
    private val client: HttpClient
) {
    suspend fun upload(base64: String, name: String): ImgurUpload {
        val response = client.submitForm(formParameters = parameters {
            append("album", "YuKcQI3mEuYFu3q")
            append("image", base64)
            append("type", "base64")
            append("name", name)
        }) {
            url {
                url("https://api.imgur.com/3/image")
                headers.append("Authorization", "Client-ID 5becc567d624bcf")
                headers.append("Accept", "application/json")
            }
        }

        if (response.status.isSuccess()) {
            return response.body<UploadResponse>().data
        } else {
            throw IllegalStateException("Failed with code: ${response.status.value} - ${response.bodyAsText()}")
        }
    }
}