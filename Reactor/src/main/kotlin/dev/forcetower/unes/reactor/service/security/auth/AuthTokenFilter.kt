package dev.forcetower.unes.reactor.service.security.auth

import dev.forcetower.unes.reactor.data.repository.RoleRepository
import dev.forcetower.unes.reactor.data.repository.UserRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class AuthJWTAuthenticationConverter(
    private val service: AuthTokenService,
    private val users: UserRepository,
    private val roles: RoleRepository
) : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        return mono {
            val authorization = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return@mono null
            if (!authorization.startsWith("Bearer ")) return@mono null
            val token = authorization.substring(7)
            val userId = service.validateToken(token) ?: return@mono null
            val user = users.findById(UUID.fromString(userId)) ?: return@mono null
            val roles = roles.findRolesByUserId(user.id).map { SimpleGrantedAuthority("ROLE_${it.name.uppercase()}") }
            UsernamePasswordAuthenticationToken(user, null, roles)
        }
    }
}

@Component
class JWTAuthenticationManager : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
    }
}
