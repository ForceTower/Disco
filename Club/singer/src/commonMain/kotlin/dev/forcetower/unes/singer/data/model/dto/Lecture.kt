package dev.forcetower.unes.singer.data.model.dto

import dev.forcetower.unes.singer.data.model.base.LectureDTO

data class Lecture(
    val ordinal: Int,
    val situation: Int,
    val date: String?,
    val subject: String?,
    val materials: List<LectureMaterial>
) {
    companion object {
        internal fun fromDTO(lecture: LectureDTO): Lecture {
            return Lecture(
                lecture.ordinal,
                lecture.situation,
                lecture.date?.trim(),
                lecture.subject?.trim(),
                lecture.materials?.items?.map { material ->
                    LectureMaterial(
                        material.id,
                        material.description.trim(),
                        material.url.link.href
                    )
                } ?: emptyList()
            )
        }
    }
}