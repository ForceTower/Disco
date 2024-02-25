package dev.forcetower.unes.reactor.utils.extension

import kotlinx.coroutines.CancellationException

inline fun <T> Result<T>.onException(action: (Throwable) -> Unit): Result<T> {
    val e = exceptionOrNull()
    when {
        e is CancellationException -> throw e
        e != null -> action(e)
    }
    return this
}