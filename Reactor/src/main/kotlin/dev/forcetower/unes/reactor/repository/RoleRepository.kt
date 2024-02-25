package dev.forcetower.unes.reactor.repository

import dev.forcetower.unes.reactor.domain.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, String> {
    @Query("SELECT R FROM roles R WHERE R.basic = true")
    fun findBasicRoles(): Collection<Role>
}