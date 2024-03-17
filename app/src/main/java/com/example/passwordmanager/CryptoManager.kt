package com.example.passwordmanager

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.KeyStore.SecretKeyEntry
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

fun ByteArray.encodeBase64(): String {
    return android.util.Base64.encodeToString(this, android.util.Base64.DEFAULT)
}

fun String.decodeBase64(): ByteArray {
    return android.util.Base64.decode(this, android.util.Base64.DEFAULT)
}

object CryptoManager {

    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    private const val ALIAS = "secret"

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(ALIAS, null) as? SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(input: String): String {
        /*val encryptedBytes = encryptCipher.doFinal(bytes)
        outputStream.use {
            it.write(encryptCipher.iv.size)
            it.write(encryptCipher.iv)
            it.write(encryptedBytes.size)
            it.write(encryptedBytes)
        }
        return encryptedBytes*/

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))

        //val iv = encryptCipher.iv
        val iv = cipher.iv


        val result = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, result, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, result, iv.size, encryptedBytes.size)

        return result.encodeBase64()
    }

    fun decrypt(input: String): String {
        /*return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptedBytesSize = it.read()
            val encryptedBytes = ByteArray(encryptedBytesSize)
            it.read(encryptedBytes)

            getDecryptCipherForIv(iv).doFinal(encryptedBytes)
        }*/

        val inputBytes = input.decodeBase64()

        val iv = inputBytes.copyOfRange(0, 16)
        val encryptedBytes = inputBytes.copyOfRange(16, inputBytes.size)

        val cipher = getDecryptCipherForIv(iv)

        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes, Charsets.UTF_8)
    }

    /*private lateinit var KEY: SecretKey // ключ для шифрования

    init {
        generateKey()
    }

    fun generateKey() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(128) // Устанавливаем длину ключа в 128 бит
        KEY = keyGen.generateKey()
    }

    fun encrypt(password: String): String {
        val key: SecretKey = KEY
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(password.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(encryptedPassword: String): String {
        val key: SecretKey = KEY
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decryptedBytes = cipher.doFinal(Base64.decode(encryptedPassword, Base64.DEFAULT))
        return String(decryptedBytes)
    }*/


}