package dev.forcetower.unes.club.domain.model.disciplines

sealed class SemesterFetchState {
    data object Completed : SemesterFetchState()
    data class Failed(val reason: Exception): SemesterFetchState()
}