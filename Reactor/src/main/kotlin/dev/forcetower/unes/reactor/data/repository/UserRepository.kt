package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Student
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
    @Query("INSERT INTO users(username, name, email) values (:username, :name, :email) ON CONFLICT DO NOTHING ")
    suspend fun insert(username: String, name: String, email: String?)
    suspend fun findUserByUsername(username: String): User?

    suspend fun findUserByEmail(email: String): User?

    @Query("SELECT * FROM student")
    suspend fun findUpdatableStudent(): Collection<Student>

    @Query("UPDATE users SET image_url = :link WHERE id = :id")
    suspend fun updateImage(id: UUID, link: String)
}