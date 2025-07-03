package com.krzhi.utils.auth

import com.google.gson.Gson
import com.krzhi.utils.crypt.AesUtils
import com.krzhi.utils.crypt.RsaUtils
import org.jose4j.jwk.RsaJsonWebKey
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class Jwt {
    companion object {
        private val gson = Gson()
    }

    @Value("\${jwt.aes-key}")
    private lateinit var aesKey: String
    @Value("\${jwt.key-id}")
    private lateinit var keyId: String
    @Value("\${jwt.private-key}")
    private lateinit var privateKey: String

    fun createAuthToken(info: AuthInfo): String {
        val claims = JwtClaims().apply {
            setGeneratedJwtId()
            setIssuedAtToNow()
            setExpirationTimeMinutesInTheFuture(30 * 24 * 60f)
            setNotBeforeMinutesInThePast(1f)
            subject = "u:${info.userId}"
            audience = listOf("auth")

            setClaim("auth", AesUtils.CBC.encrypt(gson.toJson(info), aesKey))
        }

        val signature = JsonWebSignature().apply {
            algorithmHeaderValue = AlgorithmIdentifiers.RSA_USING_SHA256
            keyIdHeaderValue = keyId
            payload = claims.toJson()
            key = RsaUtils.getPrivateKey(privateKey)
        }

        return signature.compactSerialization
    }
}