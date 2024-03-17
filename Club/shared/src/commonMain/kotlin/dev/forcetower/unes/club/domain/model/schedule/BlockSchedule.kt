package dev.forcetower.unes.club.domain.model.schedule

data class BlockSchedule(
    val scheduleSingle: List<ProcessedClassLocation>,
    val schedule: List<BlockLine>,
    val colorsIndex: Map<String, Int>
)

data class BlockLine(
    val id: Int,
    val items: List<ProcessedClassLocation>
)
