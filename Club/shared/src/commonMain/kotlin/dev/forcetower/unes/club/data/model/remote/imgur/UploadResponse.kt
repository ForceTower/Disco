package dev.forcetower.unes.club.data.model.remote.imgur

import kotlinx.serialization.Serializable

@Serializable
internal data class UploadResponse(
    val data: ImgurUpload,
    val success: Boolean,
    val status: Int
)

@Serializable
data class ImgurUpload(
    val link: String,
    val deletehash: String
)
