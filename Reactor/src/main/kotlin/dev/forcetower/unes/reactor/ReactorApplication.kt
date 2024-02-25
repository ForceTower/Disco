package dev.forcetower.unes.reactor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
//@EnableScheduling
class ReactorApplication

fun main(args: Array<String>) {
    runApplication<ReactorApplication>(*args)
}
