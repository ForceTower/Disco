package dev.forcetower.unes.reactor.service.evaluation

import dev.forcetower.unes.reactor.data.entity.User
import dev.forcetower.unes.reactor.domain.dto.evaluation.EvaluationPatchData
import org.springframework.stereotype.Service

@Service
class EvaluationPatcherService {
    suspend fun patch(user: User, data: EvaluationPatchData) {
        // not required.
    }
}