package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.ZonedDateTime
import java.util.UUID

@Table("discipline_offers")
data class DisciplineOffer(
    @Id
    val id: UUID,
    val disciplineId: UUID,
    val semesterId: UUID,
    val createdAt: ZonedDateTime
)
