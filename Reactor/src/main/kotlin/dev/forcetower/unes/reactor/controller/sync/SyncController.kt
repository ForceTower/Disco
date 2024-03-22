package dev.forcetower.unes.reactor.controller.sync

import dev.forcetower.unes.reactor.data.repository.StudentRepository
import dev.forcetower.unes.reactor.data.repository.SyncRegistryRepository
import dev.forcetower.unes.reactor.domain.dto.BaseResponse
import dev.forcetower.unes.reactor.domain.dto.sync.PublicSyncRegistry
import dev.forcetower.unes.reactor.utils.spring.requireUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/sync")
class SyncController(
    private val repository: SyncRegistryRepository,
    private val students: StudentRepository
) {
    @GetMapping("/history")
    suspend fun me(): ResponseEntity<BaseResponse> {
        val user = requireUser()
        val student = students.findByUserId(user.id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")

        val id = student.id ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")

        val registry = repository.findAllFromStudent(id).map {
            PublicSyncRegistry.createFrom(it)
        }
        return ResponseEntity.ok(BaseResponse.ok(registry))
    }
}