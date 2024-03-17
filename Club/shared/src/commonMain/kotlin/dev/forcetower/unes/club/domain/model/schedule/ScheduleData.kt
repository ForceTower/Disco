package dev.forcetower.unes.club.domain.model.schedule

data class ScheduleData(
    val original: Map<Int, List<ProcessedClassLocation>>,
    val block: BlockSchedule,
    val line: LineSchedule
)