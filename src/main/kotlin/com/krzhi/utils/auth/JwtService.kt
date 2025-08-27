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
class JwtService {
    companion object {
        private const val CLAIM_NAME_PRO_EXPIRE_AT = "pexpat"
    }

    @Value("\${spring.jwt.secret}")
    private lateinit var secret: String

    fun createAuthToken(info: UserAuthInfo): String {
        return Jwts.builder()
            .setIssuedAt(Date())
            .setSubject("${info.userId}")
            .setAudience("user")
            .setExpiration(Date(System.currentTimeMillis() + 30.days.inWholeMilliseconds))
            .claim(CLAIM_NAME_PRO_EXPIRE_AT, info.proExpireAt)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

    fun parseAuthToken(token: String): UserAuthInfo? {
        return try {
            val claims = parseJwt(token) ?: return null
            if (claims.audience != "user") return null

            UserAuthInfo(
                userId = claims.subject.toLong(),
                proExpireAt = claims.get(CLAIM_NAME_PRO_EXPIRE_AT, Long::class.java),
            )
        } catch (t: Throwable) {
            log.error("parse auth token exception", t)
            null
        }
    }

    private fun parseJwt(token: String): Claims? {
        return try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token)?.body
        } catch (t: Throwable) {
            log.warn("JWT token verification failed", t)
            null
        }
    }
}