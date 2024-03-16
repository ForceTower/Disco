package dev.forcetower.unes.singer.data.model.dto

data class MessagesDataPage(
    val messages: List<Message>,
    val next: String?
)