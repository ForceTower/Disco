package dev.forcetower.unes.reactor.service.image

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jose4j.base64url.Base64
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO


@Component
class ImageManipulationComponent {

    suspend fun loadImageAndResize(base64: String): BufferedImage = withContext(Dispatchers.IO) {
        val data = Base64.decode(base64)
        val bais = ByteArrayInputStream(data)
        val buffered = ImageIO.read(bais)
        val result = resizeImage(buffered)
        result
    }

    suspend fun resizeImage(original: BufferedImage): BufferedImage = withContext(Dispatchers.IO) {
        val resizedImage = BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(original, 0, 0, 400, 400, null)
        graphics2D.dispose()
        resizedImage
    }
}