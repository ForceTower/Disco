package dev.forcetower.unes.club.domain.model.auth

sealed class ServiceAuthResult {
    data object Connected : ServiceAuthResult()
    data object MissingCredential : ServiceAuthResult()
    data object RejectedCredential : ServiceAuthResult()
    data class ConnectionFailed(val reason: String) : ServiceAuthResult()
    data class UnknownError(val reason: String, val cause: Throwable) : ServiceAuthResult()
}