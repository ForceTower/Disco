package dev.forcetower.unes.reactor.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Table(name = "roles")
@Entity(name = "roles")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String,
    val name: String,
    val basic: Boolean,
    @ManyToMany(mappedBy = "roles")
    val users: Collection<User>
)