package dev.forcetower.unes.reactor.repository

import dev.forcetower.unes.reactor.domain.entity.PasskeyCredential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PasskeyRepository : JpaRepository<PasskeyCredential, String> {

}