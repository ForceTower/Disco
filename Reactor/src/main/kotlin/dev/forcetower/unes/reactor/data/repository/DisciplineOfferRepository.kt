package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.DisciplineOffer
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DisciplineOfferRepository : CoroutineCrudRepository<DisciplineOffer, UUID> {
    @Query("INSERT INTO discipline_offers(discipline_id, semester_id) VALUES (:disciplineId, :semesterId) ON CONFLICT DO NOTHING RETURNING id")
    suspend fun insertIgnore(
        disciplineId: UUID,
        semesterId: UUID
    ): UUID

    suspend fun findByDisciplineIdAndSemesterId(disciplineId: UUID, semesterId: UUID): DisciplineOffer?
}