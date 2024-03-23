package dev.forcetower.unes.reactor.service.aws.s3

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jose4j.base64url.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


@Service
class UploadToS3Service(
    private val client: S3Client,
    @Value("\${amazon.s3.bucket.name}") private val bucketName: String
) {
    suspend fun uploadProfileImage(image: BufferedImage, name: String): PutObjectResponse = withContext(Dispatchers.IO) {
        val os = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", os)

        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(name)
            .contentType("image/jpeg")
            .build()

        client.putObject(request, RequestBody.fromBytes(os.toByteArray()))
    }
}