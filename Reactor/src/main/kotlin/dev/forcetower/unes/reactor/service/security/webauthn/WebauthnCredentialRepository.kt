package dev.forcetower.unes.reactor.service.security.webauthn

import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RegisteredCredential
import com.yubico.webauthn.data.ByteArray
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor
import com.yubico.webauthn.data.PublicKeyCredentialType
import com.yubico.webauthn.data.exception.Base64UrlException
import dev.forcetower.unes.reactor.domain.entity.PasskeyCredential
import dev.forcetower.unes.reactor.repository.PasskeyRepository
import dev.forcetower.unes.reactor.repository.UserRepository
import dev.forcetower.unes.reactor.utils.base64.YubicoUtils
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import java.util.Optional
import kotlin.jvm.optionals.getOrDefault

@Component
@Transactional
class WebauthnCredentialRepository(
    private val users: UserRepository,
    private val credentials: PasskeyRepository
) : CredentialRepository {
    override fun getCredentialIdsForUsername(username: String): Set<PublicKeyCredentialDescriptor> {
        val credentials = users.findUserByUsername(username)?.credentials ?: emptySet()
        return credentials.map { it.toPublicKeyCredentialDescriptor() }.toSet()
    }

    override fun getUserHandleForUsername(username: String): Optional<ByteArray> {
        val handle = users.findUserByUsername(username)?.id?.let { id ->
            YubicoUtils.toByteArray(id)
        }
        return Optional.ofNullable(handle)
    }

    override fun getUsernameForUserHandle(userHandle: ByteArray): Optional<String> {
        val username = users.findUserById(YubicoUtils.toUUIDStr(userHandle))?.email
        return Optional.ofNullable(username)
    }

    override fun lookup(credentialId: ByteArray, userHandle: ByteArray): Optional<RegisteredCredential> {
        val id = YubicoUtils.toUUIDStr(userHandle)
        val result = users.findUserById(id)?.let { user ->
            user.credentials.firstOrNull { credentialId == ByteArray.fromBase64Url(it.keyId) }
        }?.toRegisteredCredential()
        return Optional.ofNullable(result)
    }

    override fun lookupAll(credentialId: ByteArray): Set<RegisteredCredential> {
        return credentials.findById(credentialId.base64Url)
            .map { setOf(it.toRegisteredCredential()) }
            .getOrDefault(emptySet())
    }

    private fun PasskeyCredential.toRegisteredCredential(): RegisteredCredential {
        try {
            return RegisteredCredential.builder()
                .credentialId(ByteArray.fromBase64Url(keyId))
                .userHandle(YubicoUtils.toByteArray(/*user.id*/"sodhsfiou"))
                .publicKeyCose(ByteArray.fromBase64Url(publicKeyCose))
                .build()
        } catch (e: Base64UrlException) {
            throw java.lang.RuntimeException(e)
        }
    }

    private fun PasskeyCredential.toPublicKeyCredentialDescriptor(): PublicKeyCredentialDescriptor {
        try {
            return PublicKeyCredentialDescriptor.builder()
                .id(ByteArray.fromBase64Url(keyId))
                .type(PublicKeyCredentialType.valueOf(keyType))
                .build()
        } catch (e: Base64UrlException) {
            throw RuntimeException(e)
        }
    }
}