package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "passkey")
data class Passkey(
    @Id
    private val id: UUID,
    @Column("key_id")
    val keyId: String,
    @Column("key_type")
    val keyType: String,
    @Column("public_key_cose")
    val publicKeyCose: String,
    @Column("user_id")
    val userId: UUID
) : Persistable<UUID> {
    @Transient
    private var isNew = false
    override fun getId() = if (isNew) null else id

    override fun isNew() = isNew

    fun setNew() { this.isNew = true }
}
