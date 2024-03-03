package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "role")
data class Role(
    @Id
    val id: String,
    val name: String,
    val basic: Boolean
)