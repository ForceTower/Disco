package dev.forcetower.unes.club.domain.model.auth

sealed class ServiceLinkEmailCompleteResult {
    data object Success : ServiceLinkEmailCompleteResult()
    data object InvalidCode : ServiceLinkEmailCompleteResult()
    data class Error(val code: Int, val reason: String) : ServiceLinkEmailCompleteResult()
}