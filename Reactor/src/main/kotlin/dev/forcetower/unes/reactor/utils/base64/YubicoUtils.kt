package dev.forcetower.unes.reactor.utils.base64

import java.nio.ByteBuffer
import java.util.UUID

object YubicoUtils {
    fun toByteArray(uuidStr: String): com.yubico.webauthn.data.ByteArray {
        val uuid = UUID.fromString(uuidStr)
        val buffer = ByteBuffer.wrap(ByteArray(16))
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        return com.yubico.webauthn.data.ByteArray(buffer.array())
    }

    fun toUUIDStr(byteArray: com.yubico.webauthn.data.ByteArray): String {
        val byteBuffer = ByteBuffer.wrap(byteArray.bytes)
        val high = byteBuffer.getLong()
        val low = byteBuffer.getLong()
        return UUID(high, low).toString()
    }

    fun toUUID(byteArray: com.yubico.webauthn.data.ByteArray): UUID {
        val byteBuffer = ByteBuffer.wrap(byteArray.bytes)
        val high = byteBuffer.getLong()
        val low = byteBuffer.getLong()
        return UUID(high, low)
    }
}