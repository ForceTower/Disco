package dev.forcetower.unes.reactor.service.security.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
@EnableWebFluxSecurity
class AuthConfiguration(
    private val jwtAuthenticationManager: JWTAuthenticationManager
) {
    @Bean
    fun securityFilterChain(
        http: ServerHttpSecurity,
        converter: AuthJWTAuthenticationConverter,
    ): SecurityWebFilterChain {
        val filter = AuthenticationWebFilter(jwtAuthenticationManager)
        filter.setServerAuthenticationConverter(converter)
        return http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .authorizeExchange { exchange ->
                exchange
                    .pathMatchers(HttpMethod.POST, "api/auth/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "api/auth/**").permitAll()
                    .pathMatchers("/error").permitAll()
                    .pathMatchers(".well-known/**").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}