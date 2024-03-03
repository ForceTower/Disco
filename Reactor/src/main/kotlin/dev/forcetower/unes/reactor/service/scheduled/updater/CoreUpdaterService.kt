package dev.forcetower.unes.reactor.service.scheduled.updater

import dev.forcetower.breaker.Orchestra
import dev.forcetower.breaker.result.Outcome
import dev.forcetower.unes.reactor.data.entity.User
import dev.forcetower.unes.reactor.repository.UserRepository
import dev.forcetower.unes.reactor.utils.extension.onException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@Service
class CoreUpdaterService @Autowired constructor(
    private val repository: UserRepository,
    private val orchestra: Orchestra,
    private val scope: CoroutineScope
) {
    private val logger = LoggerFactory.getLogger(CoreUpdaterService::class.java)

    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    suspend fun execute() {
        logger.info("Starting updater for rate 15")

        val users = withContext(Dispatchers.IO) {
            repository.getUpdatableUsers(15)
        }
        logger.info("Will update ${users.size} users in this batch")

        val job = scope.launch {
            val channel = produce(capacity = 5) {
                users.forEach { send(it) }
            }

            for (i in 1..5) {
                launch { processor(channel) }
            }
        }

        logger.info("Waiting for updates to complete")
        job.join()
        logger.info("Finished updating users")
    }

    private suspend fun processor(channel: ReceiveChannel<User>) {
        channel.consumeEach {
            runCatching {
                processUser(it)
            }.onException {
                logger.error("Failed to update user", it)
            }
        }
    }

    private suspend fun processUser(user: User) {
        logger.info("Running update for user ${user.id}")
        runCatching {
            val messagesOutcome = orchestra.messages(15)
            (messagesOutcome as? Outcome.Success)?.let { success ->
                val messages = success.value
//                userMessageProcessService.host(messages, user, false).execute()
            }

            if (messagesOutcome is Outcome.Error) {
                logger.debug("Messages error code: ${messagesOutcome.code}", messagesOutcome.error)
            }
        }
    }
}