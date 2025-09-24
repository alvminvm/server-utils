package com.krzhi.utils.auth

import com.krzhi.utils.annotation.Slf4j
import com.krzhi.utils.annotation.Slf4j.Companion.log
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey
import kotlin.time.Duration.Companion.days

@Slf4j
@Component
class JwtService {
    companion object {
        private const val CLAIM_NAME_PRO_EXPIRE_AT = "pexpat"
    }

    @Value("\${spring.jwt.secret}")
    private lateinit var secret: String

    fun createAuthToken(info: UserAuthInfo): String {
        return Jwts.builder()
            .issuedAt(Date())
            .subject("${info.userId}")
            .audience().add("user").and()
            .expiration(Date(System.currentTimeMillis() + 30.days.inWholeMilliseconds))
            .claim(CLAIM_NAME_PRO_EXPIRE_AT, info.proExpireAt)
            .encryptWith(getSigningKey(), Jwts.ENC.A128CBC_HS256)
            .compact()
    }

    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun parseAuthToken(token: String): UserAuthInfo? {
        return try {
            val claims = parseJwt(token) ?: return null
            if (!claims.audience.contains("user")) return null

            val proExpireAt = claims[CLAIM_NAME_PRO_EXPIRE_AT] as? Long
                ?: (claims[CLAIM_NAME_PRO_EXPIRE_AT] as? Int)?.toLong()
                ?: 0

            UserAuthInfo(
                userId = claims.subject.toLong(),
                proExpireAt = proExpireAt,
            )
        } catch (t: Throwable) {
            log.error("parse auth token exception", t)
            null
        }
    }

    private fun parseJwt(token: String): Claims? {
        return try {
            Jwts.parser()
                .decryptWith(getSigningKey())
                .build()
                .parseEncryptedClaims(token)
                ?.payload
        } catch (t: Throwable) {
            log.warn("JWT token verification failed", t)
            null
        }
    }
}