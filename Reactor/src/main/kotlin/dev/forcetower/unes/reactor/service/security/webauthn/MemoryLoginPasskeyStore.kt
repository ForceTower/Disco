package dev.forcetower.unes.reactor.service.security.webauthn

import com.yubico.webauthn.AssertionRequest
import org.apache.commons.collections4.map.PassiveExpiringMap

class MemoryLoginPasskeyStore : TokenStore<String, AssertionRequest> {
    private val store: MutableMap<String, AssertionRequest> = PassiveExpiringMap(ONE_MINUTE)

    override fun create(key: String, value: AssertionRequest) {
        store[key] = value
    }

    override fun fetch(key: String): AssertionRequest? {
        return store[key]
    }

    companion object {
        private const val ONE_MINUTE = (60 * 1000).toLong()
    }
}