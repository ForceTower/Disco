package dev.forcetower.unes.reactor.service.notification

import com.google.api.core.ApiFuture
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.ApsAlert
import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import dev.forcetower.unes.reactor.data.entity.Message
import dev.forcetower.unes.reactor.data.entity.MessagingToken
import dev.forcetower.unes.reactor.data.entity.Student
import dev.forcetower.unes.reactor.data.model.aggregation.GradeData
import dev.forcetower.unes.reactor.data.repository.MessagingTokenRepository
import dev.forcetower.unes.reactor.data.repository.UserSettingsRepository
import jdk.internal.org.jline.utils.Colors.s
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.suspendCancellableCoroutine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.coroutines.resume

@Service
class UserNotificationService(
    private val messaging: FirebaseMessaging,
    private val messagingTokens: MessagingTokenRepository,
    private val userSettings: UserSettingsRepository
) {
    private val logger = LoggerFactory.getLogger(UserNotificationService::class.java)
    suspend fun notifyMessages(messages: List<Message>, student: Student) {
        if (messages.isEmpty()) return
        val tokens = messagingTokens.getMessagingTokensByStudentId(student.id!!)

        if (tokens.isEmpty()) {
            logger.debug("No one to notify about message")
            return
        }

        if (messages.size > 2) {
            onReceiveMultipleMessages(tokens, messages.size)
        } else {
            val elements = tokens.map { it.token }
            val futures = messages.map {
                val message = multicastMessage(it.findTitle(), it.content, elements)
                messaging.sendEachForMulticastAsync(message)
            }

            val results = futures.map { it.await() }
            results.forEach { batch ->
                logger.debug("Sent ${batch.successCount} messages. ${batch.failureCount} failed.")
                processBatch(batch, tokens)
            }
        }
    }

    suspend fun notifyGrades(newGrades: List<GradeData>, student: Student) {
        if (newGrades.isEmpty()) return
        val tokens = messagingTokens.getMessagingTokensByStudentId(student.id!!)
        if (tokens.isEmpty()) {
            logger.debug("No one to notify about message")
            return
        }

        newGrades.forEach {
            grade -> notifyGrade(grade, tokens)
        }
    }

    private suspend fun notifyGrade(grade: GradeData, tokens: List<MessagingToken>) {
        when (grade.notificationState) {
            1 -> sendEvaluationCreated(grade, tokens)
            2 -> sendEvaluationDateChanged(grade, tokens)
            3 -> sendEvaluationGradePosted(grade, tokens)
            4 -> sendEvaluationGradeValueChanged(grade, tokens)
            else -> return
        }
    }

    private suspend fun sendEvaluationGradeValueChanged(grade: GradeData, tokens: List<MessagingToken>) {
        val title = "Nota alterada"
        tokens.forEach { token ->
            val spoilerLevel = userSettings.findByUserId(token.userId)?.gradeSpoiler ?: 3
            val valueRaw = grade.valueRaw
            val value = grade.value
            val body = when {
                spoilerLevel.toInt() == 3 && valueRaw != null -> {
                    "A nota da ${grade.name} da disciplina ${grade.discipline} foi alterada para $valueRaw"
                }
                spoilerLevel.toInt() == 2 && value != null -> {
                    if (value > 8.0.toBigDecimal()) {
                        "Boas notícias. A nota da ${grade.name} da disciplina ${grade.discipline} foi alterada"
                    } else if (value >= 7.0.toBigDecimal()) {
                        "A nota da ${grade.name} da disciplina ${grade.discipline} foi alterada para uma nota passável"
                    } else {
                        "A nota da ${grade.name} da disciplina ${grade.discipline} foi alterada. Mas talvez não seja o esperado"
                    }
                }
                else -> "A nota da ${grade.name} da disciplina ${grade.discipline} foi alterada"
            }

            val message = multicastMessage(title, body, listOf(token.token))
            sendAndProcessMulticast(message, listOf(token))
        }
    }

    private suspend fun sendEvaluationDateChanged(grade: GradeData, tokens: List<MessagingToken>) {
        val title = "Data de avaliação modificada"
        val body = "A data da avaliação ${grade.name} da disciplina ${grade.discipline} foi alterada"
        val message = multicastMessage(title, body, tokens.map { it.token })
        sendAndProcessMulticast(message, tokens)
    }

    private suspend fun sendEvaluationCreated(grade: GradeData, tokens: List<MessagingToken>) {
        val title = "Avaliação criada"
        val body = "A ${grade.name} da disciplina ${grade.discipline} foi criada mas não há notas associadas"
        val message = multicastMessage(title, body, tokens.map { it.token })
        sendAndProcessMulticast(message, tokens)
    }

    private suspend fun sendEvaluationGradePosted(grade: GradeData, tokens: List<MessagingToken>) {
        val title = "Nota postada"
        tokens.forEach { token ->
            val spoilerLevel = userSettings.findByUserId(token.userId)?.gradeSpoiler ?: 3
            val valueRaw = grade.valueRaw
            val value = grade.value
            val body = when {
                spoilerLevel.toInt() == 3 && valueRaw != null -> {
                    "Você tirou $valueRaw na avaliação ${grade.name} disciplina ${grade.discipline}"
                }
                spoilerLevel.toInt() == 2 && value != null -> {
                    if (value > 9.9.toBigDecimal()) { // 10
                        "A nota da ${grade.name} da disciplina ${grade.discipline} está disponível. Foi perfeito."
                    } else if (value >= 8.0.toBigDecimal()) { // [8 -> 9.9]
                        "A nota da ${grade.name} da disciplina ${grade.discipline} está disponível. Tudo está bem e em paz."
                    } else if (value >= 7.0.toBigDecimal()) { // [7, 7.9[
                        "A nota da ${grade.name} da disciplina ${grade.discipline} está disponível. Foi ok."
                    } else if (value >= 6.0.toBigDecimal()) { // [6, 6.9[
                        "A nota da ${grade.name} da disciplina ${grade.discipline} está disponível. Dá para recuperar."
                    } else if (value >= 5.0.toBigDecimal()) { // [5, 5.9[
                        "A nota da ${grade.name} da disciplina ${grade.discipline} está disponível. Mais algumas dessas e passa fácil na final"
                    } else { // [4.9, -inf
                        "A nota da ${grade.name} da disciplina ${grade.discipline} está disponível. Me desculpe pela má noticia."
                    }
                }
                else -> "A nota da ${grade.name} da disciplina ${grade.discipline} está disponível"
            }

            val message = multicastMessage(title, body, listOf(token.token))
            sendAndProcessMulticast(message, listOf(token))
        }
    }

    private suspend fun onReceiveMultipleMessages(tokens: List<MessagingToken>, messageCount: Int) {
        val title = "Novas mensagens!"
        val body = "Você tem $messageCount mensagens novas! Confira o conteúdo no app!"
        val message = multicastMessage(title, body, tokens.map { it.token })
        sendAndProcessMulticast(message, tokens)
    }

    private suspend fun UserNotificationService.sendAndProcessMulticast(
        message: MulticastMessage,
        tokens: List<MessagingToken>
    ) {
        val batch = messaging.sendEachForMulticastAsync(message).await()
        processBatch(batch, tokens)
    }

    private suspend fun processBatch(
        batch: BatchResponse,
        tokens: List<MessagingToken>
    ) {
        logger.debug("Sent ${batch.successCount} messages. ${batch.failureCount} failed.")
        val invalid = batch.responses.mapIndexed { index, response ->
            if (!response.isSuccessful) tokens[index].id else null
        }.filterNotNull()

        messagingTokens.deleteAllById(invalid)
    }

    private fun multicastMessage(
        title: String,
        body: String,
        tokens: List<String>
    ): MulticastMessage {
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build()

        val message = MulticastMessage.builder()
            .setNotification(notification)
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .build()
            )
            .setApnsConfig(
                ApnsConfig.builder()
                    .setAps(
                        Aps.builder()
                            .setAlert(
                                ApsAlert.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                            )
                            .setSound("default")
                            .build()
                    )
                    .build()
            )
            .addAllTokens(tokens)
            .build()
        return message
    }

    private suspend fun <T> ApiFuture<T>.await() = suspendCancellableCoroutine<T> { continuation ->
        continuation.invokeOnCancellation {
            this.cancel(true)
        }

        addListener({
            @Suppress("BlockingMethodInNonBlockingContext")
            continuation.resume(get())
        }, Dispatchers.IO.asExecutor())
    }
}