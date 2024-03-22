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
import dev.forcetower.unes.reactor.data.repository.MessagingTokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.suspendCancellableCoroutine
import org.springframework.stereotype.Service
import kotlin.coroutines.resume

@Service
class UserNotificationService(
    private val messaging: FirebaseMessaging,
    private val messagingTokens: MessagingTokenRepository
) {
    suspend fun notifyMessages(messages: List<Message>, student: Student) {
        if (messages.isEmpty()) return
        val tokens = messagingTokens.getMessagingTokensByStudentId(student.id!!)

        if (tokens.isEmpty()) {
            println("No one to notify about message")
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
                println("Sent ${batch.successCount} messages. ${batch.failureCount} failed.")
                processBatch(batch, tokens)
            }
        }
    }

    private suspend fun onReceiveMultipleMessages(tokens: List<MessagingToken>, messageCount: Int) {
        val title = "Novas mensagens!"
        val body = "Você tem $messageCount mensagens novas! Confira o conteúdo no app!"
        val message = multicastMessage(title, body, tokens.map { it.token })
        val batch = messaging.sendEachForMulticastAsync(message).await()
        processBatch(batch, tokens)
    }

    private suspend fun processBatch(
        batch: BatchResponse,
        tokens: List<MessagingToken>
    ) {
        println("Sent ${batch.successCount} messages. ${batch.failureCount} failed.")
        val invalid = batch.responses.mapIndexed { index, response ->
            if (!response.isSuccessful) tokens[index].id else null
        }.filterNotNull()

        messagingTokens.deleteAllById(invalid)
    }

    private fun multicastMessage(
        title: String,
        body: String,
        tokens: List<String>
    ): MulticastMessage? {
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