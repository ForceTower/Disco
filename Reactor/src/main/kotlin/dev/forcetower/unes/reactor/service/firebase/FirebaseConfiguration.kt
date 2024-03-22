package dev.forcetower.unes.reactor.service.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader


@Configuration
class FirebaseConfiguration(
    private val resourceLoader: ResourceLoader
) {
    @Bean
    fun createApp(): FirebaseApp {
        val resource = resourceLoader.getResource("classpath:firebase/unes-uefs-firebase.json")

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(resource.inputStream))
            .setDatabaseUrl("https://unes-uefs.firebaseio.com")
            .build()

        return FirebaseApp.initializeApp(options)
    }
    @Bean
    fun messaging(app: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(app)
    }
}