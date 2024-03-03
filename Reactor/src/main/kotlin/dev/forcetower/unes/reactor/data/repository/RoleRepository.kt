package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Role
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
interface RoleRepository : CoroutineCrudRepository<Role, String> {
    @Query("SELECT R FROM role R WHERE R.basic = true")
    suspend fun findBasicRoles(): Mono<Collection<Role>>

    @Query("SELECT R FROM role R INNER JOIN user_roles UR ON UR.role_id = R.id WHERE UR.user_id = \$1")
    suspend fun findRolesByUserId(userId: UUID): Collection<Role>
}