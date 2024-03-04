package dev.forcetower.unes.reactor.service.email

import com.resend.Resend
import com.resend.services.emails.model.CreateEmailOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class EmailSenderComponent(
    @Value("\${unes.email.resend.api-key}") private val apiKey: String
) {
    private val logger = LoggerFactory.getLogger(EmailSenderComponent::class.java)

    suspend fun send(email: String, subject: String, html: String) {
        val resend = Resend(apiKey)

        val sendEmailRequest = CreateEmailOptions.builder()
            .from("no-reply@unes.forcetower.dev")
            .to(email)
            .subject(subject)
            .html(html)
            .build()

        withContext(Dispatchers.IO) {
            resend.emails().send(sendEmailRequest)
        }
    }
}