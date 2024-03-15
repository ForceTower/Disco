package dev.forcetower.unes.club

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform