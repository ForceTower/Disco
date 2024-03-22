package dev.forcetower.unes.reactor.service.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.FileInputStream


@Configuration
class FirebaseConfiguration {
    @Bean
    fun createApp(): FirebaseApp {
        val resource = ClassPathResource("unes-uefs-firebase.json").getFile()
        val serviceAccount = FileInputStream(resource)

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://unes-uefs.firebaseio.com")
            .build()

        return FirebaseApp.initializeApp(options)
    }
    @Bean
    fun messaging(app: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(app)
    }
}