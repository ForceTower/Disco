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
import dev.forcetower.unes.reactor.data.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthorizationService @Autowired constructor(
    private val users: UserRepository,
    private val relyingParty: RelyingParty
) : ReactiveUserDetailsService {
    override fun findByUsername(username: String): Mono<UserDetails> {
        return mono {
            users.findUserByUsername(username) ?: throw UsernameNotFoundException("user not found")
        }
    }

    fun startAssertion(request: PasskeyStartAssertionRequest): AssertionRequest {
        val options = StartAssertionOptions.builder()
            .timeout(60_000)
            .userVerification(UserVerificationRequirement.REQUIRED)
            .build()

        return relyingParty.startAssertion(options)
    }

    fun finishAssertion(
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