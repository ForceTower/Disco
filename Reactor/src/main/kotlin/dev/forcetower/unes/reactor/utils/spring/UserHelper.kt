package dev.forcetower.unes.reactor.utils.spring

import dev.forcetower.unes.reactor.data.entity.User
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder

suspend fun requireUser(): User {
    return ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User
}