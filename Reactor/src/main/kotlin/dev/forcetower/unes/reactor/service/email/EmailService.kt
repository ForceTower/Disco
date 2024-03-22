package dev.forcetower.unes.reactor.service.email

import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.security.SecureRandom

@Service
class EmailService(
    private val sender: EmailSenderComponent,
    private val templateEngine: TemplateEngine
) {
    suspend fun sendEmailVerificationCode(
        to: String,
        code: String
    ) {
        val context = Context().apply {
            setVariable("code", code)
            setVariable("email", to)
        }

        val html = templateEngine.process("verify_email", context)
            .replace("\${code}", code).replace("\${email}", to)
        sender.send(to, "Login no UNES", html)
    }

    fun generateID(size: Int): String {
        val rnd = SecureRandom()
        val digits = CharArray(size)
        digits[0] = '1' + (rnd.nextInt(9))
        for (i in 1 until digits.size) {
            digits[i] = '0' + (rnd.nextInt(10))
        }
        return String(digits)
    }
}