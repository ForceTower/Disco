package dev.forcetower.unes.singer.data.model.dto

data class MessageDiscipline(
    val disciplineId: Long,
    val classId: Long,
    val code: String,
    val discipline: String,
    val group: String
)