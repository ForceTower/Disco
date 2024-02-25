package dev.forcetower.unes.reactor.service.scheduled.updater

import dev.forcetower.breaker.Orchestra
import dev.forcetower.breaker.model.Authorization
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@Configuration
class CoreUpdaterConfig {
    @Bean("updaterExecutors")
    fun executor(): CoroutineDispatcher {
        return Executors.newFixedThreadPool(5)
            .asCoroutineDispatcher()
            .limitedParallelism(5)
    }

    @Bean("updaterOkHttp")
    fun client(): OkHttpClient {
        return OkHttpClient.Builder()
            .callTimeout(1, TimeUnit.MINUTES)
            .build()
    }

    @Bean("updaterScope")
    fun scope(
        @Autowired executor: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(executor)
    }

    @Bean
    fun orchestra(@Autowired client: OkHttpClient): Orchestra {
        val orchestra = Orchestra.Builder()
            .client(client)
            .build()

        orchestra.setAuthorization(Authorization("", ""))
        return orchestra
    }
}