package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Discipline
import dev.forcetower.unes.reactor.data.entity.DisciplineClass
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DisciplineClassRepository : CoroutineCrudRepository<DisciplineClass, UUID> {
    @Query("INSERT INTO classes(offer_id, sequence, platform_id, credits_override) VALUES (:offerId, :groupName, :platformId, :hours) ON CONFLICT(offer_id,sequence) DO NOTHING RETURNING id")
    suspend fun insertIgnore(offerId: UUID, groupName: String, platformId: Long, hours: Int): UUID

    suspend fun findByPlatformId(platformId: Long): DisciplineClass?
}