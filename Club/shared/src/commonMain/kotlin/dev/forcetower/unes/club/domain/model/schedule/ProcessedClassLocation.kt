package dev.forcetower.unes.club.domain.model.schedule

import com.benasher44.uuid.uuid4

sealed class ProcessedClassLocation(val id: String = uuid4().toString()) {
    data class EmptySpace(val special: Boolean = false) : ProcessedClassLocation()
    data class TimeSpace(val start: String, val end: String, val startInt: Int, val endInt: Int) : ProcessedClassLocation()
    data class DaySpace(val day: String, val dayInt: Int) : ProcessedClassLocation()
    data class ElementSpace(val reference: ClassLocationData) : ProcessedClassLocation()
}

sealed class LinedClassLocation(val id: String = uuid4().toString()) {
    data class DaySpace(val day: String, val dayInt: Int) : LinedClassLocation()
    data class ElementSpace(val reference: ClassLocationData) : LinedClassLocation()
}