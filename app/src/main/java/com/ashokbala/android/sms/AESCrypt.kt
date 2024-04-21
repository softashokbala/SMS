package com.ashokbala.android.sms

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

object AESCrypt {

    private const val AES_TRANSFORMATION = "AES/ECB/PKCS5Padding"
    private const val AES_KEY_SIZE = 256

    // Generate a random AES key
    fun generateKey(): SecretKeySpec {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(AES_KEY_SIZE, SecureRandom())
        val secretKey = keyGenerator.generateKey()
        return SecretKeySpec(secretKey.encoded, "AES")
    }

    // Encrypt the message with AES
    fun encrypt(message: String, key: String): String {
        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(Base64.decode(key, Base64.DEFAULT), "AES"))
        val encryptedBytes = cipher.doFinal(message.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    // Decrypt the message with AES
    fun decrypt(encryptedMessage: String, key: String): String {
        try {
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(Base64.decode(key, Base64.DEFAULT), "AES"))
            val decryptedBytes = cipher.doFinal(Base64.decode(encryptedMessage, Base64.DEFAULT))
            return String(decryptedBytes, Charsets.UTF_8)
        }catch (e:Exception)
        {
            return ""
        }

    }
}

