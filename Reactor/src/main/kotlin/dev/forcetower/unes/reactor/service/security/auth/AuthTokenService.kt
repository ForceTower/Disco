package dev.forcetower.unes.reactor.service.security.auth

import dev.forcetower.unes.reactor.data.entity.Role
import dev.forcetower.unes.reactor.data.entity.User
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jwk.JsonWebKey
import org.jose4j.jwk.RsaJsonWebKey
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.InvalidJwtException
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AuthTokenService(
    @Value("\${unes.security.token.private-key}") private val privateKeyJson: String
) {
    private val logger = LoggerFactory.getLogger(AuthTokenService::class.java)
    private val rsaJsonWebKey = JsonWebKey.Factory.newJwk(privateKeyJson) as RsaJsonWebKey

    fun generateToken(user: User, authorities: Collection<Role>, expiration: Float): String {
        val claims = JwtClaims().apply {
            audience = listOf("urn:unes")
            issuer = "unes.forcetower.dev"
            setIssuedAtToNow()
            setExpirationTimeMinutesInTheFuture(expiration)
            setGeneratedJwtId(16)
            subject = user.id.toString()
            setClaim("urn:unes:data", mapOf(
                "roles" to authorities.map { it.name }
            ))
        }

        val jws = JsonWebSignature()
        jws.payload = claims.toJson()
        jws.key = rsaJsonWebKey.privateKey
        jws.keyIdHeaderValue = rsaJsonWebKey.keyId
        jws.algorithmHeaderValue = AlgorithmIdentifiers.RSA_USING_SHA256
        return jws.compactSerialization
    }

    fun validateToken(token: String): String? {
        try {
            val consumer = JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setExpectedIssuer("unes.forcetower.dev")
                .setExpectedAudience("urn:unes")
                .setVerificationKey(rsaJsonWebKey.key)
                .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
                .build()

            val claims = consumer.processToClaims(token)
            return claims.subject
        } catch (err: InvalidJwtException) {
            return null
        }
    }

    fun printKeys() {
        val jwkJson = rsaJsonWebKey.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE)
//        val encryptingJwe = JsonWebEncryption()
//        encryptingJwe.algorithmHeaderValue = KeyManagementAlgorithmIdentifiers.PBES2_HS384_A192KW
//        encryptingJwe.encryptionMethodHeaderParameter = ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256
//        encryptingJwe.key = PbkdfKey("2108dd5085d2dd8")
//        encryptingJwe.payload = jwkJson
//        val jweEncryptedJwk = encryptingJwe.compactSerialization

        logger.info(jwkJson)
        logger.info(rsaJsonWebKey.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY))
//        logger.info(jweEncryptedJwk)

//        val decryptingJwe = JsonWebEncryption()
//        decryptingJwe.compactSerialization = jweEncryptedJwk
//        encryptingJwe.key = PbkdfKey("2108dd5085d2dd8")
//        val payload = encryptingJwe.payload
//        val publicJsonWebKey = PublicJsonWebKey.Factory.newPublicJwk(jwkJson)
//        logger.info(payload)
//        val originalKey = RsaJsonWebKey(JsonUtil.parseJson(jwkJson))
//        val otherKey = JsonWebKey.Factory.newJwk(jwkJson)
//        logger.info(originalKey.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE))
//        logger.info(otherKey.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE))

//        logger.info(publicJsonWebKey.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY))
    }
}