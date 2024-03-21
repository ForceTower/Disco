package dev.forcetower.unes.club.data.repository.remote.edge

import dev.forcetower.unes.club.data.model.remote.edge.auth.ServiceAccessTokenDTO
import dev.forcetower.unes.club.data.service.client.AuthService
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.ServiceAccessToken
import dev.forcetower.unes.club.domain.model.auth.PasskeyAssertionData
import dev.forcetower.unes.club.domain.model.auth.ServiceAuthResult
import dev.forcetower.unes.club.domain.repository.remote.edge.AuthRepository
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

internal class AuthRepositoryImpl(
    private val database: GeneralDatabase,
    private val service: AuthService
) : AuthRepository {
    override suspend fun connected(): Boolean = withContext(Dispatchers.IO) {
        database.serviceAccessTokenQueries.selectToken().executeAsOneOrNull() != null
    }

    override suspend fun getAccess(): ServiceAccessToken?  = withContext(Dispatchers.IO) {
        database.serviceAccessTokenQueries.selectToken().executeAsOneOrNull()
    }

    override suspend fun handshake(): ServiceAuthResult = withContext(Dispatchers.IO) {
        val access = database.accessQueries.selectAccess().executeAsOneOrNull() ?: return@withContext ServiceAuthResult.MissingCredential
        val response = service.anonymous(
            access.username,
            access.password,
            "SNOWPIERCER"
        )
        if (response.status == HttpStatusCode.BadRequest ||
            response.status == HttpStatusCode.Forbidden ||
            response.status == HttpStatusCode.Unauthorized) return@withContext ServiceAuthResult.RejectedCredential

        if (response.status != HttpStatusCode.OK) {
            return@withContext ServiceAuthResult.ConnectionFailed(response.status.description)
        }

        val token = runCatching { response.body<ServiceAccessTokenDTO>() }
            .onFailure {
                return@withContext ServiceAuthResult.UnknownError(it.message ?: "Login failed", it)
            }.getOrThrow()

        database.serviceAccessTokenQueries.deleteAll()

        database.serviceAccessTokenQueries.insertReplace(
            token.accessToken, Clock.System.now().toEpochMilliseconds()
        )

        ServiceAuthResult.Connected(null)
    }

    override suspend fun passkeyAssertionStart(): PasskeyAssertionData {
        return service.passkeyAssertionStart()
    }

    override suspend fun passkeyAssertionFinish(flowId: String, credential: String) = withContext(Dispatchers.IO) {
        val response = service.passkeyAssertionFinish(flowId, credential)

        if (response.status != HttpStatusCode.OK) {
            throw IllegalStateException("Failed to load with status code ${response.status})")
        }

        val token = response.body<ServiceAccessTokenDTO>()
        database.serviceAccessTokenQueries.deleteAll()

        database.serviceAccessTokenQueries.insertReplace(
            token.accessToken, Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun deleteAuthAndAccount() = withContext(Dispatchers.IO) {
        database.serviceAccessTokenQueries.deleteAll()
        database.serviceAccountQueries.deleteAll()
    }
}
