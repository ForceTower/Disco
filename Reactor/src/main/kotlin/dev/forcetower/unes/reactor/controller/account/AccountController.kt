package dev.forcetower.unes.reactor.controller.account

import dev.forcetower.unes.reactor.data.entity.MessagingToken
import dev.forcetower.unes.reactor.data.repository.MessagingTokenRepository
import dev.forcetower.unes.reactor.data.repository.UserRepository
import dev.forcetower.unes.reactor.domain.dto.BaseResponse
import dev.forcetower.unes.reactor.domain.dto.account.ChangeAccountPictureRequest
import dev.forcetower.unes.reactor.domain.dto.account.CompleteRegisterFinish
import dev.forcetower.unes.reactor.domain.dto.account.CompleteRegisterStart
import dev.forcetower.unes.reactor.domain.dto.account.PublicPersonalAccount
import dev.forcetower.unes.reactor.domain.dto.account.UpdateFCMTokenRequest
import dev.forcetower.unes.reactor.service.aws.s3.UploadToS3Service
import dev.forcetower.unes.reactor.service.email.EmailService
import dev.forcetower.unes.reactor.service.image.ImageManipulationComponent
import dev.forcetower.unes.reactor.utils.spring.requireUser
import io.github.scru128.Scru128
import jakarta.validation.Valid
import kotlinx.coroutines.delay
import org.apache.commons.collections4.map.PassiveExpiringMap
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/account")
class AccountController(
    @Value("\${amazon.s3.bucket.baseUrl}") private val baseBucketUrl: String,
    private val storage: UploadToS3Service,
    private val imageManipulation: ImageManipulationComponent,
    private val emails: EmailService,
    private val users: UserRepository,
    private val tokens: MessagingTokenRepository
) {
    private val emailSec: MutableMap<String, String> = PassiveExpiringMap((5 * 60 * 1000).toLong())
    private val logger = LoggerFactory.getLogger(AccountController::class.java)

    @GetMapping("/me")
    suspend fun me(): ResponseEntity<BaseResponse> {
        val user = requireUser()
        return ResponseEntity.ok(
            BaseResponse.ok(
                PublicPersonalAccount(
                    user.id.toString(),
                    user.name,
                    user.email,
                    user.imageUrl
                )
            )
        )
    }

    @PostMapping("/register/start")
    suspend fun registerStart(@RequestBody @Valid body: CompleteRegisterStart): ResponseEntity<BaseResponse> {
        val user = requireUser()
        val (email, dryRun) = body
        val code = emails.generateID(10)
        val security = "sec_${Scru128.generateString()}"

        // check for + in username
        // check for . in google mails...

        val existing = users.findUserByEmail(email)
        if (existing != null) {
            logger.info("Someone just tried to register with a taken email.")
            // answer the same as email sent to avoid enumerations
            delay((370L..770L).random())
            val result = mutableMapOf("securityToken" to security)
            return ResponseEntity.ok(BaseResponse.ok(result, "Email sent"))
        }

        if (dryRun != true) {
            emails.sendEmailVerificationCode(email, code)
        }

        logger.info("Sent email with code: $code")
        emailSec["email-code-validation:${user.id}:${code}:${security}"] = email

        val result = mutableMapOf("securityToken" to security)
        return ResponseEntity.ok(BaseResponse.ok(result, "Email sent"))
    }

    @PostMapping("/register/complete")
    suspend fun registerFinish(@RequestBody @Valid body: CompleteRegisterFinish): ResponseEntity<BaseResponse> {
        val user = requireUser()
        val (code, security) = body
        val email = emailSec["email-code-validation:${user.id}:${code}:${security}"]
            ?: return ResponseEntity.badRequest().body(BaseResponse.fail("Invalid request"))

        val existing = users.findUserByEmail(email)
        if (existing != null) {
            logger.info("Someone just tried to update to a taken email.")
            ResponseEntity.badRequest().body(BaseResponse.fail("email already taken"))
        }

        users.save(user.copy(email = email))
        emailSec.remove("email-code-validation:${user.id}:${code}:${security}")
        return ResponseEntity.ok(BaseResponse.ok("Email updated!"))
    }

    @PostMapping("/fcm")
    suspend fun updateFcmToken(@RequestBody @Valid body: UpdateFCMTokenRequest): ResponseEntity<BaseResponse> {
        val user = requireUser()
        val (token) = body
        tokens.save(MessagingToken(token, user.id).apply { setNew() })
        return ResponseEntity.ok(
            BaseResponse.ok("Saved!")
        )
    }

    @PostMapping("/picture")
    suspend fun changePicture(@RequestBody @Valid body: ChangeAccountPictureRequest): ResponseEntity<BaseResponse> {
        val user = requireUser()
        val name = "profile/${user.id}/images/${Scru128.generateString()}.jpg"

        val image = imageManipulation.loadImageAndResize(body.base64)
        storage.uploadProfileImage(image, name)
        users.updateImage(user.id, "${baseBucketUrl}/${name}")
        return ResponseEntity.ok(BaseResponse.ok("Updated image"))
    }

}