package dev.forcetower.unes.reactor.data.model.aggregation

import java.math.BigDecimal
import java.util.UUID

data class GradeData(
    val id: UUID,
    val name: String,
    val notificationState: Int,
    val value: BigDecimal?,
    val valueRaw: String?,
    val discipline: String,
    val disciplineCode: String,
    val semester: String
)