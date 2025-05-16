package com.krzhi.utils.crypt

import org.apache.commons.codec.binary.Base64
import org.apache.commons.lang3.StringUtils
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES工具类
 */
enum class AesUtils(private val pattern: String) {
    CBC("AES/CBC/PKCS5Padding"), ECB("AES/ECB/PKCS5Padding");

    private fun checkKeyAndIv(key: String?, iv: String?) {
        require(!(StringUtils.isBlank(key) || key!!.length != 16)) { "Invalid key" }
        require(!(StringUtils.isNotBlank(iv) && iv!!.length != 16)) { "Invalid iv" }
    }

    fun encrypt(data: String, key: String, iv: String? = null): String {
        checkKeyAndIv(key, iv)
        val bytes = encrypt0(pattern, key, data.toByteArray(StandardCharsets.UTF_8), iv)
        return Base64.encodeBase64String(bytes)
    }

    fun decrypt(data: String?, key: String, iv: String? = null): String {
        checkKeyAndIv(key, iv)
        val bytes = decrypt0(pattern, key, Base64.decodeBase64(data), iv)
        return String(bytes, StandardCharsets.UTF_8)
    }

    private fun encrypt0(pattern: String, key: String, data: ByteArray, iv: String?): ByteArray {
        try {
            val cipher = Cipher.getInstance(pattern)
            val secretKeySpec = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), ALG)
            if (iv.isNullOrBlank()) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            } else {
                val ivParameterSpec = IvParameterSpec(iv.toByteArray(StandardCharsets.UTF_8))
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
            }

            return cipher.doFinal(data)
        } catch (e: Throwable) {
            throw e as? IllegalArgumentException ?: IllegalArgumentException(e)
        }
    }

    private fun decrypt0(pattern: String, key: String, data: ByteArray, iv: String?): ByteArray {
        try {
            val cipher = Cipher.getInstance(pattern)
            val secretKeySpec = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), ALG)
            if (StringUtils.isNotBlank(iv)) {
                val ivParameterSpec = IvParameterSpec(iv!!.toByteArray(StandardCharsets.UTF_8))
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            }

            return cipher.doFinal(data)
        } catch (e: Exception) {
            throw e as? IllegalArgumentException ?: IllegalArgumentException(e)
        }
    }

    companion object {
        private const val ALG = "AES"
    }
}
