package com.krzhi.utils.auth

import com.krzhi.utils.annotation.Slf4j
import com.krzhi.utils.annotation.Slf4j.Companion.log
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import kotlin.time.Duration.Companion.days

@Slf4j
@Component
class Jwt {
    companion object {
        private const val CLAIM_PRO_EXPIRE_AT = "pexpat"
    }

    @Value("\${jwt.aes-key}")
    private lateinit var aesKey: String
    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    fun createAuthToken(info: AuthInfo): String {
        return Jwts.builder()
            .setIssuedAt(Date())
            .setSubject("${info.userId}")
            .setAudience("user")
            .setExpiration(Date(System.currentTimeMillis() + 30.days.inWholeMilliseconds))
            .claim(CLAIM_PRO_EXPIRE_AT, info.proExpireAt)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }

    fun parseAuthToken(token: String): AuthInfo? {
        return try {
            val claims = parseJwt(token) ?: return null
            if (claims.audience != "user") return null
            AuthInfo(
                userId = claims.subject.toLong(),
                proExpireAt = claims.get(CLAIM_PRO_EXPIRE_AT, Long::class.java),
            )
        } catch (t: Throwable) {
            log.error("parse auth token exception", t)
            null
        }
    }

    private fun parseJwt(token: String): Claims? {
        return try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)?.body
        } catch (t: Throwable) {
            log.warn("JWT token verification failed", t)
            null
        }
    }
}