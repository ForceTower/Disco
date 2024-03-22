package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID
import java.time.OffsetDateTime

@Table("classes")
data class DisciplineClass(
    @Id
    val id: UUID,
    @Column("offer_id")
    val offerId: UUID,
    val sequence: String,
    val platformId: Long,
    @Column("credits_override")
    val creditsOverride: Int?,
    @Column("created_at")
    val createdAt: OffsetDateTime? = null
)