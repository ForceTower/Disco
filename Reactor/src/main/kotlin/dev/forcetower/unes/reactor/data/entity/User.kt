package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.util.UUID

@Table(name = "users")
data class User(
    @Id
    val id: UUID,
    val name: String,
    private val username: String,
    val email: String?,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return emptySet() //roles.map { SimpleGrantedAuthority("ROLE_${it.name.uppercase()}") }
    }

    override fun getUsername(): String {
        return username
    }
    override fun getPassword() = null
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}