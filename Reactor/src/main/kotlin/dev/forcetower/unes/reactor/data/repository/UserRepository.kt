package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.User
import org.jose4j.jwk.Use
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
interface UserRepository : CoroutineCrudRepository<User, UUID> {
    @Query("INSERT INTO users(username, name, email) values (:username, :name, :email)")
    suspend fun insert(username: String, name: String, email: String?): User
    suspend fun findUserByUsername(username: String): User?

    suspend fun findUserByEmail(email: String): User?

    @Query("SELECT u FROM users u")
    suspend fun getUpdatableUsers(rate: Int): Collection<User>
}