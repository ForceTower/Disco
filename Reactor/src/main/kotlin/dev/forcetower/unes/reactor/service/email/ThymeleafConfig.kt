package dev.forcetower.unes.reactor.service.email

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver


@Configuration
class ThymeleafConfig {
    @Bean
    fun templateResolver(): ClassLoaderTemplateResolver {
        val resolver = ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            isCacheable = false
            suffix = ".html"
            setTemplateMode("HTML")
            characterEncoding = "UTF-8"
        }
        return resolver
    }

//    @Bean
//    fun templateEngine(): SpringTemplateEngine {
//        val engine = SpringTemplateEngine()
//        engine.setTemplateResolver(templateResolver())
//        return engine
//    }
}