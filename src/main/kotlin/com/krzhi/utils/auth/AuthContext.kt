package com.krzhi.utils.auth

import com.google.gson.Gson
import io.grpc.Context
import io.grpc.Metadata
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

@Component
class AuthContext {
    companion object {
        private val METADATA_KEY_AUTHORIZATION = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)
        private val CONTEXT_KEY_AUTH = Context.key<AuthInfo?>("auth")

        @Value("\${auth.secret-key}")
        private var secretKey = "kecAGmTm3jDGCyNJ"
        private val gson = Gson()

        fun createToken(info: AuthInfo): String {
            val json = gson.toJson(info)
            return encrypt(json)
        }

        private fun decrypt(encrypted: String): String {
            val key = SecretKeySpec(secretKey.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, key)

            val decoded = Base64.getDecoder().decode(encrypted)
            val decrypted = cipher.doFinal(decoded)
            return String(decrypted)
        }

        private fun encrypt(plain: String): String {
            val key = SecretKeySpec(secretKey.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val encrypted = cipher.doFinal(plain.toByteArray())
            val encoded = Base64.getEncoder().encodeToString(encrypted)
            return encoded
        }
    }

    private val log = LoggerFactory.getLogger(AuthContext::class.java)

    val auth: AuthInfo? get() = CONTEXT_KEY_AUTH.get()
    val userId: Long? get() = auth?.userId

    fun auth(metadata: Metadata): Context? {
        val token = metadata[METADATA_KEY_AUTHORIZATION]?.replace("bearer ", "", ignoreCase = true)
        if (token.isNullOrBlank()) {
            log.warn("token not found")
            return null
        }

        // token aes 解密
        val json = try {
            decrypt(token)
        } catch (t: Throwable) {
            log.warn("token decrypt failed", t)
            return null
        }

        // json 解析
        val info = try {
            gson.fromJson(json, AuthInfo::class.java)
        } catch (t: Throwable) {
            log.warn("token parse failed", t)
            return null
        }

        val ctx = Context.current().withValue(CONTEXT_KEY_AUTH, info)
        return ctx
    }
}