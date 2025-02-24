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

        private val METADATA_KEY_SCOPE = Metadata.Key.of("Scope", Metadata.ASCII_STRING_MARSHALLER)
        private val CONTEXT_KEY_SCOPE = Context.key<Map<String, String>?>("scope")

        @Value("\${auth.secret-key}")
        private var authSecretKey = "kecAGmTm3jDGCyNJ"
        @Value("\${scope.secret-key}")
        private var scopeSecretKey = "kecAGmTm3jDGCyNJ"

        private val gson = Gson()

        fun createToken(info: AuthInfo): String {
            val json = gson.toJson(info)
            return encrypt(json, authSecretKey)
        }

        fun createScope(data: Map<String, String>): String {
            val json = gson.toJson(data)
            return encrypt(json, scopeSecretKey)
        }

        private fun decrypt(encrypted: String, secretKey: String): String {
            val key = SecretKeySpec(secretKey.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key)

            val decoded = Base64.getDecoder().decode(encrypted)
            val decrypted = cipher.doFinal(decoded)
            return String(decrypted)
        }

        private fun encrypt(plain: String, secretKey: String): String {
            val key = SecretKeySpec(secretKey.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val encrypted = cipher.doFinal(plain.toByteArray())
            val encoded = Base64.getEncoder().encodeToString(encrypted)
            return encoded
        }
    }

    private val log = LoggerFactory.getLogger(AuthContext::class.java)

    val auth: AuthInfo? get() = CONTEXT_KEY_AUTH.get()
    val userId: Long? get() = auth?.userId

    val scope get() = CONTEXT_KEY_SCOPE.get() ?: mapOf()

    fun auth(metadata: Metadata, context: Context? = null): Context? {
        val token = metadata[METADATA_KEY_AUTHORIZATION]?.replace("bearer ", "", ignoreCase = true)
        if (token.isNullOrBlank()) {
            log.warn("token not found")
            return null
        }

        // token aes 解密
        val json = try {
            decrypt(token, authSecretKey)
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

        val ctx = (context ?: Context.current()).withValue(CONTEXT_KEY_AUTH, info)
        return ctx
    }

    fun scope(metadata: Metadata, context: Context? = null): Context? {
        val scope = parseScope(metadata)
        val ctx = (context ?: Context.current()).withValue(CONTEXT_KEY_SCOPE, scope)
        return ctx
    }

    private fun parseScope(metadata: Metadata): Map<String, String> {
        val encrypted = metadata[METADATA_KEY_SCOPE] ?: return mapOf()
        val scope = try {
            decrypt(encrypted, scopeSecretKey)
        } catch (t: Throwable) {
            log.warn("scope decrypt failed", t)
            return mapOf()
        }

        return try {
            val map = gson.fromJson(scope, Map::class.java)
            map.map { (k, v) -> k.toString() to v.toString() }.toMap()
        } catch (t: Throwable) {
            log.warn("scope parse failed", t)
            mapOf()
        }
    }
}