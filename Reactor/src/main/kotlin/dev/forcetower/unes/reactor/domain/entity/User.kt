package dev.forcetower.unes.reactor.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Table(name = "users")
@Entity(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String,
    val name: String,
    private val username: String,
    private val password: String,
    val email: String,
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: Collection<Role> = emptyList(),
    @OneToMany(fetch = FetchType.EAGER)
    val credentials: Collection<PasskeyCredential> = emptyList()
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val roles = listOf("user")
        return roles.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }
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