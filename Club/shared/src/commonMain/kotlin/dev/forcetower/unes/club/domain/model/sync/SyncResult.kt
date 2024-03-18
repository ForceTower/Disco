package dev.forcetower.unes.club.domain.model.sync

sealed class SyncResult {
    data object InvalidCredentials : SyncResult()
    data class LoginError(val error: String, val exception: Exception) : SyncResult()
    data class OtherError(val error: String, val exception: Exception) : SyncResult()
    data object Completed : SyncResult()
    data object NoOp : SyncResult()
    data object InvalidSemester : SyncResult()
}