package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.MessagingToken
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MessagingTokenRepository : CoroutineCrudRepository<MessagingToken, UUID>