package dev.forcetower.unes.reactor.service.security.auth

import com.yubico.webauthn.AssertionRequest
import com.yubico.webauthn.AssertionResult
import com.yubico.webauthn.FinishAssertionOptions
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.StartAssertionOptions
import com.yubico.webauthn.data.AuthenticatorAssertionResponse
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs
import com.yubico.webauthn.data.PublicKeyCredential
import com.yubico.webauthn.data.UserVerificationRequirement
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyStartAssertionRequest
import dev.forcetower.unes.reactor.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthorizationService @Autowired constructor(
    private val users: UserRepository,
    private val relyingParty: RelyingParty
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return users.findUserByUsername(username) ?: throw UsernameNotFoundException("user not found")
    }

    suspend fun startAssertion(request: PasskeyStartAssertionRequest): AssertionRequest {
        val options = StartAssertionOptions.builder()
            .timeout(60_000)
            .userVerification(UserVerificationRequirement.REQUIRED)
            .build()

        return relyingParty.startAssertion(options)
    }

    suspend fun finishAssertion(
        request: AssertionRequest,
        response: PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs>
    ): AssertionResult {
        val options = FinishAssertionOptions.builder()
            .request(request)
            .response(response)
            .build()

        return relyingParty.finishAssertion(options)
    }
}