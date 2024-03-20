package dev.forcetower.unes.club.domain.model.auth

import dev.forcetower.unes.club.data.storage.database.ServiceAccount

sealed class ServiceAuthResult {
    data class Connected(val account: ServiceAccount?) : ServiceAuthResult()
    data object MissingCredential : ServiceAuthResult()
    data object RejectedCredential : ServiceAuthResult()
    data class ConnectionFailed(val reason: String) : ServiceAuthResult()
    data class UnknownError(val reason: String, val cause: Throwable) : ServiceAuthResult()
}