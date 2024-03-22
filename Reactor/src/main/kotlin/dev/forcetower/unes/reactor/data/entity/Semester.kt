package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.ZonedDateTime
import java.util.UUID

@Table("semesters")
data class Semester(
    @Id
    val id: UUID,
    val name: String,
    val codename: String,
    @Column("platform_id")
    val platformId: Long,
    val start: ZonedDateTime?,
    val end: ZonedDateTime?,
    val startClass: ZonedDateTime?,
    val endClass: ZonedDateTime?
)
