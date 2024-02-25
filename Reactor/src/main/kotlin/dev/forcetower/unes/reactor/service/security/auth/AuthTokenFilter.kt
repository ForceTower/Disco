package dev.forcetower.unes.reactor.service.security.auth

import dev.forcetower.unes.reactor.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class AuthTokenFilter(
    private val service: AuthTokenService,
    private val users: UserRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorization = request.getHeader("Authorization")
        val token = authorization?.replace("Bearer ", "") ?: return filterChain.doFilter(request, response)
        val userId = service.validateToken(token) ?: return filterChain.doFilter(request, response)
        val user = users.findUserById(userId) ?: return filterChain.doFilter(request, response)

        val authentication = UsernamePasswordAuthenticationToken(user, null, user.authorities)
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }
}