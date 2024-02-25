package dev.forcetower.unes.reactor.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Table(name = "passkey_credentials")
@Entity(name = "passkey_credentials")
data class PasskeyCredential(
    @Id
    val keyId: String,
    val keyType: String,
    val publicKeyCose: String,
    @ManyToOne
    val user: User
)
