package dev.forcetower.unes.singer.data.model.dto

data class LectureMissed(
    val id: Long,
    val accredited: Boolean,
    val retroactive: Boolean,
    val lecture: Lecture
)