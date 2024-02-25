package dev.forcetower.unes.reactor.service.security.webauthn

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
import org.apache.commons.collections4.map.PassiveExpiringMap

class MemoryRegisterPasskeyStore : TokenStore<String, PublicKeyCredentialCreationOptions> {
    private val store: MutableMap<String, PublicKeyCredentialCreationOptions> = PassiveExpiringMap(ONE_MINUTE)

    override fun create(key: String, value: PublicKeyCredentialCreationOptions) {
        store[key] = value
    }

    override fun fetch(key: String): PublicKeyCredentialCreationOptions? {
        return store[key]
    }

    companion object {
        private const val ONE_MINUTE = (60 * 1000).toLong()
    }
}