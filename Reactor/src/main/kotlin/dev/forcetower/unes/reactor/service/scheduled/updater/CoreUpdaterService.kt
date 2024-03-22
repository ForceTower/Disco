package dev.forcetower.unes.reactor.service.scheduled.updater

import dev.forcetower.unes.reactor.data.entity.Student
import dev.forcetower.unes.reactor.data.repository.SyncRegistryRepository
import dev.forcetower.unes.reactor.data.repository.UserRepository
import dev.forcetower.unes.reactor.data.repository.UserSettingsRepository
import dev.forcetower.unes.reactor.service.snowpiercer.SnowpiercerUpdateService
import dev.forcetower.unes.reactor.utils.extension.onException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@Service
class CoreUpdaterService @Autowired constructor(
    private val repository: UserRepository,
    private val settingsRepository: UserSettingsRepository,
    private val syncRegistry: SyncRegistryRepository,
    private val scope: CoroutineScope,
    private val updater: SnowpiercerUpdateService
) {
    private val logger = LoggerFactory.getLogger(CoreUpdaterService::class.java)

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    suspend fun execute() {
        logger.info("Starting updater for rate 30")

        // add rate
        val students = repository.findUpdatableStudent()
        logger.info("Will update ${students.size} students in this batch")

        val job = scope.launch {
            val channel = produce(capacity = 5) {
                students.forEach { send(it) }
            }

            for (i in 1..5) {
                launch { processor(channel) }
            }
        }

        logger.info("Waiting for updates to complete")
        job.join()
        logger.info("Finished updating users")
    }

    private suspend fun processor(channel: ReceiveChannel<Student>) {
        channel.consumeEach {
            runCatching {
                processUser(it)
            }.onException {
                logger.error("Failed to update user", it)
            }
        }
    }

    private suspend fun processUser(student: Student) {
        logger.info("Running update for user ${student.userId}")
        settingsRepository.createForUser(student.userId)
        val completed = settingsRepository.findByUserId(student.userId)?.initialSyncCompleted ?: false
        val syncId = syncRegistry.createRegistry(student.id!!, "SS Piercer")
        runCatching {
            updater.update(student, completed)
            settingsRepository.updateInitialSyncForUser(student.userId, true)
        }.onSuccess {
            syncRegistry.updateRegistry(syncId, true, 0, "Atualização completa")
        }.onFailure {
            syncRegistry.updateRegistry(syncId, true, 1, "Erro ${it.message}")
        }.getOrThrow()
        logger.info("Completed update for user ${student.userId}")
    }
}