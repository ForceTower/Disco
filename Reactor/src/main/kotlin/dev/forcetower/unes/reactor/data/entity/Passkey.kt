package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "passkey")
data class Passkey(
    @Id
    val id: String,
    @Column("key_id")
    val keyId: String,
    @Column("key_type")
    val keyType: String,
    @Column("public_key_cose")
    val publicKeyCose: String,
    @Column("user_id")
    val userId: UUID
)
