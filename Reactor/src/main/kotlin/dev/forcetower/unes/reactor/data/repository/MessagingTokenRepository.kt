package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.MessagingToken
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MessagingTokenRepository : CoroutineCrudRepository<MessagingToken, UUID> {

    @Query("SELECT umt.* FROM user_messaging_token umt INNER JOIN users u ON u.id = umt.user_id INNER JOIN student s ON u.id = s.user_id WHERE s.id = :studentId")
    suspend fun getMessagingTokensByStudentId(studentId: UUID): List<MessagingToken>
}