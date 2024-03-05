package dev.forcetower.unes.reactor.service.security.webauthn

import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.data.RelyingPartyIdentity
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebAuthnConfig(
    @Value("\${unes.relying-party.id}") private val id: String,
    @Value("\${unes.relying-party.name}") private val name: String,
) {
    @Bean
    fun relyingParty(credentials: CredentialRepository): RelyingParty {
        val identity = RelyingPartyIdentity.builder()
            .id(id)
            .name(name)
            .build()

        val relyingParty = RelyingParty.builder()
            .identity(identity)
            .credentialRepository(credentials)
            .allowOriginPort(true)
            .origins(
                setOf(
                    "android:apk-key-hash:yWiOua1OxKtoVzZiQwlGK8qkL5IZZsk_hzt4dXmVsr4" // android debug
                )
            )
            .build()

        return relyingParty
    }

    @Bean
    fun loginRequestStore(): MemoryLoginPasskeyStore {
        return MemoryLoginPasskeyStore()
    }

    @Bean
    fun registerRequestStore(): MemoryRegisterPasskeyStore {
        return MemoryRegisterPasskeyStore()
    }
}