package dev.forcetower.unes.singer.data.model.dto

data class Message(
    val id: Long,
    val content: String,
    val sender: String,
    val timestamp: String,
    val senderType: Int,
    val discipline: MessageDiscipline?
)