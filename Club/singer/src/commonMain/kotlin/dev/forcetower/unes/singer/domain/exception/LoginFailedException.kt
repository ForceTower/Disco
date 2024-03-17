package dev.forcetower.unes.singer.domain.exception

open class LoginFailedException(message: String, cause: Throwable? = null) : Exception(message, cause)