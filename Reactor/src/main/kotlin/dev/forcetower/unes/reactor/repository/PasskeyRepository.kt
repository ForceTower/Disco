package dev.forcetower.unes.reactor.repository

import dev.forcetower.unes.reactor.data.entity.Passkey
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PasskeyRepository : CoroutineCrudRepository<Passkey, String> {
    @Query("SELECT P.* FROM passkey P INNER JOIN users U ON U.id = P.user_id WHERE U.email = \$1")
    suspend fun findPasskeyByUserEmail(email: String): Collection<Passkey>

    @Query("SELECT P.* FROM passkey P WHERE P.user_id = \$1")
    suspend fun findPasskeyByUserId(userId: String): Collection<Passkey>
}