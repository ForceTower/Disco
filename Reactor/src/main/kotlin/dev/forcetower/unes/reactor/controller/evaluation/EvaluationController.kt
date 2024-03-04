package dev.forcetower.unes.reactor.controller.evaluation

import dev.forcetower.unes.reactor.domain.dto.BaseResponse
import dev.forcetower.unes.reactor.domain.dto.evaluation.EvaluationPatchData
import dev.forcetower.unes.reactor.service.evaluation.EvaluationPatcherService
import dev.forcetower.unes.reactor.utils.spring.requireUser
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/evaluation")
class EvaluationController(
    private val patcher: EvaluationPatcherService
) {

    @PostMapping("/patch")
    suspend fun save(@RequestBody @Valid body: EvaluationPatchData): ResponseEntity<BaseResponse> {
        val user = requireUser()
        patcher.patch(user, body)
        return ResponseEntity.ok(BaseResponse.ok("Grades patched"))
    }
}