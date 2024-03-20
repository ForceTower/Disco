package dev.forcetower.unes.club.domain.usecase.account

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.model.auth.ServiceLinkEmailCompleteResult
import dev.forcetower.unes.club.domain.repository.remote.edge.AccountRepository

class LinkEmailUseCase internal constructor(
    private val account: AccountRepository
) {
    @NativeCoroutines
    suspend fun registerEmail(email: String): String {
        return account.registerEmail(email)
    }

    @NativeCoroutines
    suspend fun completeEmailRegister(code: String, security: String): ServiceLinkEmailCompleteResult {
        return account.completeEmailRegister(code, security)
    }
}