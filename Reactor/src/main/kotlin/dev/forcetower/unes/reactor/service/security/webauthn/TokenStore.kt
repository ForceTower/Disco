package dev.forcetower.unes.reactor.service.security.webauthn

interface TokenStore<K, V> {
    fun create(key: K, value: V)
    fun fetch(key: K): V?
}