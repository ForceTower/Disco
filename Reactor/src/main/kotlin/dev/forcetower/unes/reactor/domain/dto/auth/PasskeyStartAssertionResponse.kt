package dev.forcetower.unes.reactor.domain.dto.auth

import dev.forcetower.unes.reactor.data.model.PasskeyAssert

data class PasskeyStartAssertionResponse(
    val flowId: String,
    val challenge: PasskeyAssert
)