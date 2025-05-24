package com.krzhi.utils.crypt

import com.krzhi.utils.annotation.Slf4j
import com.krzhi.utils.annotation.Slf4j.Companion.log
import org.apache.commons.codec.binary.Base64
import org.springframework.util.Assert
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


/**
 * RSA加解密工具类
 */
@Slf4j
object RsaUtils {
    private val CHARSET: Charset = StandardCharsets.UTF_8

    /**
     * 密钥算法
     */
    private const val ALGORITHM_RSA: String = "RSA"
    private const val ALGORITHM_RSA_SIGN: String = "SHA256WithRSA"

    /**
     * keySize
     */
    private const val ALGORITHM_RSA_PRIVATE_KEY_LENGTH: Int = 2048

    /**
     * 初始化RSA算法密钥对
     *
     * @return 经过Base64编码后的公私钥Map, 键名分别为publicKey和privateKey
     */
    private fun generateKeyPair(): MutableMap<String?, String> {
        //为RSA算法创建一个KeyPairGenerator对象
        val kpg: KeyPairGenerator
        try {
            kpg = KeyPairGenerator.getInstance(ALGORITHM_RSA)
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalArgumentException("No such algorithm-->[$ALGORITHM_RSA]")
        }
        //初始化KeyPairGenerator对象,
        kpg.initialize(ALGORITHM_RSA_PRIVATE_KEY_LENGTH)
        //生成密匙对
        val keyPair = kpg.generateKeyPair()
        //得到公钥
        val publicKey: Key = keyPair.public
        val publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.encoded)
        //得到私钥
        val privateKey: Key = keyPair.private
        val privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.encoded)
        val keyPairMap: MutableMap<String?, String> = HashMap()
        keyPairMap.put("publicKey", publicKeyStr)
        keyPairMap.put("privateKey", privateKeyStr)
        return keyPairMap
    }

    private fun getPublicKey(key: String): PublicKey? {
        try {
            val keyBytes = Base64.decodeBase64(key.toByteArray(StandardCharsets.UTF_8))
            val keySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(ALGORITHM_RSA)
            return keyFactory.generatePublic(keySpec)
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }

    private fun getPrivateKey(key: String): PrivateKey? {
        try {
            val keyBytes = Base64.decodeBase64(key.toByteArray(StandardCharsets.UTF_8))
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(ALGORITHM_RSA)
            return keyFactory.generatePrivate(keySpec)
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }

    /**
     * RSA算法公钥加密数据
     *
     * @param data 待加密的明文字符串
     * @param key  RSA公钥字符串
     * @return RSA公钥加密后的经过Base64编码的密文字符串
     */
    fun encryptByPublicKey(data: String, key: String): String {
        try {
            val publicKey: Key? = getPublicKey(key)
            //encrypt
            val cipher = Cipher.getInstance(ALGORITHM_RSA)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            val bytes = rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.toByteArray(CHARSET))
            return Base64.encodeBase64URLSafeString(bytes)
        } catch (e: Exception) {
            throw RuntimeException("加密字符串[$data]时遇到异常", e)
        }
    }

    /**
     * RSA算法公钥解密数据
     *
     * @param data 待解密的经过Base64编码的密文字符串
     * @param key  RSA公钥字符串
     * @return RSA公钥解密后的明文字符串
     */
    fun decryptByPublicKey(data: String?, key: String): String {
        try {
            val publicKey = getPublicKey(key)
            //decrypt
            val cipher = Cipher.getInstance(ALGORITHM_RSA)
            cipher.init(Cipher.DECRYPT_MODE, publicKey)
            val bytes = rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data))
            return String(bytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            throw RuntimeException("解密字符串[$data]时遇到异常", e)
        }
    }

    /**
     * RSA算法私钥加密数据
     *
     * @param data 待加密的明文字符串
     * @param key  RSA私钥字符串
     * @return RSA私钥加密后的经过Base64编码的密文字符串
     */
    fun encryptByPrivateKey(data: String, key: String): String {
        try {
            val privateKey = getPrivateKey(key)
            //encrypt
            val cipher = Cipher.getInstance(ALGORITHM_RSA)
            cipher.init(Cipher.ENCRYPT_MODE, privateKey)

            val bytes = rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.toByteArray(CHARSET))
            return Base64.encodeBase64String(bytes)
        } catch (e: Exception) {
            throw RuntimeException("加密字符串[$data]时遇到异常", e)
        }
    }

    /**
     * RSA算法私钥解密数据
     *
     * @param data 待解密的经过Base64编码的密文字符串
     * @param key  RSA私钥字符串
     * @return RSA私钥解密后的明文字符串
     */
    fun decryptByPrivateKey(data: String, key: String): String {
        try {
            val privateKey = getPrivateKey(key)
            //decrypt
            val cipher = Cipher.getInstance(ALGORITHM_RSA)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)

            val bytes = rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data))
            return String(bytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            throw RuntimeException("解密字符串[" + data + "]时遇到异常", e)
        }
    }

    /**
     * RSA算法使用私钥对数据生成数字签名
     *
     * @param data 待签名的明文字符串
     * @param key  RSA私钥字符串
     * @return RSA私钥签名后的经过Base64编码的字符串
     */
    fun sign(data: String, key: String): String {
        try {
            val privateKey = getPrivateKey(key)
            //sign
            val signature = Signature.getInstance(ALGORITHM_RSA_SIGN)
            signature.initSign(privateKey)
            signature.update(data.toByteArray(CHARSET))
            return Base64.encodeBase64URLSafeString(signature.sign())
        } catch (e: Exception) {
            throw RuntimeException("签名字符串[$data]时遇到异常", e)
        }
    }

    /**
     * RSA算法使用公钥校验数字签名
     *
     * @param data 参与签名的明文字符串
     * @param key  RSA公钥字符串
     * @param sign RSA签名得到的经过Base64编码的字符串
     * @return true--验签通过,false--验签未通过
     */
    fun verify(data: String, key: String, sign: String): Boolean {
        try {
            val publicKey = getPublicKey(key)
            //verify
            val signature = Signature.getInstance(ALGORITHM_RSA_SIGN)
            signature.initVerify(publicKey)
            signature.update(data.toByteArray(CHARSET))
            return signature.verify(Base64.decodeBase64(sign))
        } catch (e: Exception) {
            throw RuntimeException("验签字符串[$data]时遇到异常", e)
        }
    }

    /**
     * RSA算法分段加解密数据
     *
     * @param cipher 初始化了加解密工作模式后的javax.crypto.Cipher对象
     * @param opmode 加解密模式,值为javax.crypto.Cipher.ENCRYPT_MODE/DECRYPT_MODE
     * @return 加密或解密后得到的数据的字节数组
     */
    private fun rsaSplitCodec(cipher: Cipher, opmode: Int, datas: ByteArray): ByteArray {
        val maxBlock = if (opmode == Cipher.DECRYPT_MODE) {
            ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8
        } else {
            ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8 - 11
        }

        val out = ByteArrayOutputStream()
        out.use {
            var offset = 0
            var i = 0
            try {
                while (datas.size > offset) {
                    val buff = if (datas.size - offset > maxBlock) {
                        cipher.doFinal(datas, offset, maxBlock)
                    } else {
                        cipher.doFinal(datas, offset, datas.size - offset)
                    }
                    out.write(buff, 0, buff.size)

                    i++
                    offset = i * maxBlock
                }

                return out.toByteArray()
            } catch (e: java.lang.Exception) {
                throw RuntimeException("加解密阀值为[$maxBlock]的数据时发生异常", e)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val channelRsaKey = generateKeyPair()
        val channelPrivateKey = channelRsaKey["privateKey"]!!
        val channelPublicKey = channelRsaKey["publicKey"]!!
        log.info("channelPrivateKey: {}", channelPrivateKey)
        log.info("channelPublicKey: {}", channelPublicKey)

        val platformRsaKey = generateKeyPair()
        val platformPrivateKey = platformRsaKey["privateKey"]!!
        val platformPublicKey = platformRsaKey["publicKey"]!!
        log.info("platformPrivateKey: {}", platformPrivateKey)
        log.info("platformPublicKey: {}", platformPublicKey)

        val plainText = "{\"test\":\"test\"}"

        val encryptData = encryptByPublicKey(plainText, platformPublicKey)
        val sign = sign(encryptData, channelPrivateKey)

        val verified = verify(encryptData, channelPublicKey, sign)
        Assert.isTrue(verified, "verify fail!")

        val decrypted = decryptByPrivateKey(encryptData, platformPrivateKey)
        Assert.isTrue(decrypted == plainText, "")
    }
}
