package dev.forcetower.unes.club.data.repository.remote.edge

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.benasher44.uuid.uuid4
import dev.forcetower.unes.club.data.model.remote.edge.account.RegisterPasskeyCredential
import dev.forcetower.unes.club.data.service.client.AccountService
import dev.forcetower.unes.club.data.service.client.ImgurService
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.ServiceAccount
import dev.forcetower.unes.club.domain.exception.ServiceUnauthenticatedException
import dev.forcetower.unes.club.domain.model.account.PasskeyRegister
import dev.forcetower.unes.club.domain.model.account.RegisterPasskeyFlowStart
import dev.forcetower.unes.club.domain.model.auth.ServiceLinkEmailCompleteResult
import dev.forcetower.unes.club.domain.repository.remote.edge.AccountRepository
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class AccountRepositoryImpl(
    private val database: GeneralDatabase,
    private val service: AccountService,
    private val imgur: ImgurService,
    private val json: Json
) : AccountRepository {
    override fun getAccount(): Flow<ServiceAccount?> {
        return database.serviceAccountQueries.selectMe().asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    override suspend fun fetchAccountIfConnected(): ServiceAccount? = withContext(Dispatchers.IO) {
        database.serviceAccessTokenQueries.selectToken().executeAsOneOrNull() ?: return@withContext null
        fetchAccount()
    }

    override suspend fun registerEmail(email: String): String {
        return service.linkEmail(email)
    }

    override suspend fun completeEmailRegister(code: String, security: String): ServiceLinkEmailCompleteResult {
        val result = service.completeLinkEmail(code, security)
        runCatching { fetchAccount() }
        return result
    }

    override suspend fun fetchAccount(): ServiceAccount {
        val me = service.me()
        val next = ServiceAccount(me.id, me.name, me.email, me.imageUrl, 1)
        database.serviceAccountQueries.insertReplace(next)
        return next
    }

    override suspend fun registerPasskeyStart(): RegisterPasskeyFlowStart {
        val data = service.registerPasskeyStart()
        val register = json.decodeFromString<PasskeyRegister>(data.create)
        return RegisterPasskeyFlowStart(data.flowId, register)
    }

    override suspend fun registerPasskeyFinish(flowId: String, data: String) {
        service.registerPasskeyFinish(flowId, data)
    }

    override suspend fun registerMessagingTokenIfNeeded(token: String) {
        withContext(Dispatchers.IO) {
            database.serviceAccessTokenQueries.selectToken().executeAsOneOrNull() ?: return@withContext null
            service.registerMessagingToken(token)
        }
    }

    override suspend fun changeProfilePicture(base64: String) {
        withContext(Dispatchers.IO) {
            val result = imgur.upload(base64, "user-unes-${uuid4().toString().substring(0..5)}")
                ?: throw IllegalStateException("Failed to upload")
            service.changeProfilePicture(result.link)
            fetchAccount()
        }
    }
}