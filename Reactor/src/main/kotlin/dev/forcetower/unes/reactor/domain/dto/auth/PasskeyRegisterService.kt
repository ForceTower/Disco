package dev.forcetower.unes.reactor.domain.dto.auth

import com.yubico.webauthn.FinishRegistrationOptions
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.StartRegistrationOptions
import com.yubico.webauthn.data.AuthenticatorAttestationResponse
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
import com.yubico.webauthn.data.PublicKeyCredential
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
import com.yubico.webauthn.data.UserIdentity
import com.yubico.webauthn.data.UserVerificationRequirement
import dev.forcetower.unes.reactor.data.entity.Passkey
import dev.forcetower.unes.reactor.data.entity.User
import dev.forcetower.unes.reactor.data.repository.PasskeyRepository
import dev.forcetower.unes.reactor.utils.base64.YubicoUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PasskeyRegisterService(
    private val relyingParty: RelyingParty,
    private val passkeyRepository: PasskeyRepository
) {
    private val logger = LoggerFactory.getLogger(PasskeyRegisterService::class.java)

    fun start(user: User): PublicKeyCredentialCreationOptions {
        val options = createPublicKeyCredentialCreationOptions(user)
        return options
    }

    suspend fun finish(
        user: User,
        request: PublicKeyCredentialCreationOptions,
        response: PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>
    ) {
        val options = FinishRegistrationOptions.builder()
            .request(request)
            .response(response)
            .build()

        val result = relyingParty.finishRegistration(options)

        val credential = Passkey(
            "",
            result.keyId.id.base64Url,
            result.keyId.type.name,
            result.publicKeyCose.base64Url,
            user.id
        )

        passkeyRepository.save(credential)
    }

    private fun createPublicKeyCredentialCreationOptions(user: User): PublicKeyCredentialCreationOptions {
        val userIdentity = UserIdentity.builder()
            .name(user.email)
            .displayName(user.name)
            .id(YubicoUtils.toByteArray(user.id.toString()))
            .build()

        val authenticatorSelectionCriteria = AuthenticatorSelectionCriteria.builder()
            .userVerification(UserVerificationRequirement.REQUIRED)
            .build()

        val startRegistrationOptions = StartRegistrationOptions.builder()
            .user(userIdentity)
            .timeout(60_000)
            .authenticatorSelection(authenticatorSelectionCriteria)
            .build()

        try {
            val options = relyingParty.startRegistration(startRegistrationOptions)
            return options
        } catch (err: Exception) {
            logger.error("failed to create options!", err)
            throw err
        }
    }
}