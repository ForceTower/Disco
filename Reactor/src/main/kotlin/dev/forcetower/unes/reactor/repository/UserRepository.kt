package dev.forcetower.unes.reactor.repository

import dev.forcetower.unes.reactor.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findUserByUsername(username: String): User?

    fun findUserById(id: String): User?

    @Query("SELECT u FROM users u")
    suspend fun getUpdatableUsers(rate: Int): Collection<User>
}