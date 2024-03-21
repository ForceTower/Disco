package dev.forcetower.unes.club.domain.model.auth

import dev.forcetower.unes.club.domain.model.auth.PasskeyAssert
import kotlinx.serialization.Serializable

@Serializable
data class PasskeyAssertionData(
    val flowId: String,
    val challenge: PasskeyAssert
)