package dev.forcetower.unes.club.data.model.remote.edge.sync

import kotlinx.serialization.Serializable

@Serializable
data class PublicSyncRegistry(
    val id: String,
    val executor: String,
    val completed: Boolean,
    val success: Boolean?,
    val error: Int?,
    val message: String?,
    val startAt: String,
    val finishedAt: String?
)