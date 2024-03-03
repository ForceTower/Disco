package dev.forcetower.unes.reactor.service.snowpiercer

import dev.forcetower.breaker.Orchestra
import dev.forcetower.breaker.model.Authorization
import dev.forcetower.breaker.model.Person
import org.springframework.stereotype.Service

@Service
class SnowpiercerLoginService(
    private val orchestra: Orchestra
) {
    suspend fun login(username: String, password: String): Person? {
        val result = orchestra.login(Authorization(username, password))
        return result.success()?.value
    }
}