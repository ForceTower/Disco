package dev.forcetower.unes.reactor.service.security.webauthn

import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RegisteredCredential
import com.yubico.webauthn.data.ByteArray
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor
import com.yubico.webauthn.data.PublicKeyCredentialType
import com.yubico.webauthn.data.exception.Base64UrlException
import dev.forcetower.unes.reactor.data.entity.Passkey
import dev.forcetower.unes.reactor.data.repository.PasskeyRepository
import dev.forcetower.unes.reactor.data.repository.UserRepository
import dev.forcetower.unes.reactor.utils.base64.YubicoUtils
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class WebauthnCredentialRepository(
    private val users: UserRepository,
    private val credentials: PasskeyRepository
) : CredentialRepository {
    override fun getCredentialIdsForUsername(username: String): Set<PublicKeyCredentialDescriptor> {
        val credentials = runBlocking { credentials.findPasskeyByUserEmail(username) }
        return credentials.map { it.toPublicKeyCredentialDescriptor() }.toSet()
    }

    override fun getUserHandleForUsername(username: String): Optional<ByteArray> {
        val handle = runBlocking { users.findUserByUsername(username) }?.id?.let { id ->
            YubicoUtils.toByteArray(id.toString())
        }
        return Optional.ofNullable(handle)
    }

    override fun getUsernameForUserHandle(userHandle: ByteArray): Optional<String> {
        val username = runBlocking { users.findById(YubicoUtils.toUUID(userHandle)) }?.email
        return Optional.ofNullable(username)
    }

    override fun lookup(credentialId: ByteArray, userHandle: ByteArray): Optional<RegisteredCredential> {
        val id = YubicoUtils.toUUIDStr(userHandle)
        val credentials = runBlocking { credentials.findPasskeyByUserId(id) }
            .firstOrNull { credentialId == ByteArray.fromBase64Url(it.keyId) }
            ?.toRegisteredCredential()
        return Optional.ofNullable(credentials)
    }

    override fun lookupAll(credentialId: ByteArray): Set<RegisteredCredential> {
        return runBlocking {
            credentials.findById(credentialId.base64Url)
                ?.let { setOf(it.toRegisteredCredential()) }
                .orEmpty()
        }
    }

    private fun Passkey.toRegisteredCredential(): RegisteredCredential {
        try {
            return RegisteredCredential.builder()
                .credentialId(ByteArray.fromBase64Url(keyId))
                .userHandle(YubicoUtils.toByteArray(userId.toString()))
                .publicKeyCose(ByteArray.fromBase64Url(publicKeyCose))
                .build()
        } catch (e: Base64UrlException) {
            throw java.lang.RuntimeException(e)
        }
    }

    private fun Passkey.toPublicKeyCredentialDescriptor(): PublicKeyCredentialDescriptor {
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