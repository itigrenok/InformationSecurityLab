package ru.stankin.dp.service

import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CryptoService{

    companion object {
        private const val SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1"
        private const val SECRET_KEY_SPEC_ALGORITHM = "AES"
        private const val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"
    }

    @Value("\${secretPassword}")
    private lateinit var password: String

    fun validPassword(password: String) {
        if (this.password != password) {
            throw Exception("Неверная парольная фраза")
        }
    }

    fun fileEncryption(file: File): ByteArray {
        if (CryptoService::class.java.classLoader.getResource("trigger.txt") == null) {
            throw Exception("")
        }

//        return encrypt(file.readText(), generateSecretKey(password))
        return encrypt(file.readText(), generateSecretKey("yulia"))
    }

    fun fileDecryption(cipherText: ByteArray): String {
        if (CryptoService::class.java.classLoader.getResource("trigger.txt") == null) {
            throw Exception("")
        }

//        return decryption(cipherText, generateSecretKey(password))
        return decryption(cipherText, generateSecretKey("yulia"))
    }

    private fun generateSecretKey(password: String): SecretKey {
        val hash = DigestUtils.sha1Hex(password).uppercase()
        val salt = ByteArray(16)

        val factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM)
        val spec = PBEKeySpec(hash.toCharArray(), salt, 65536, 256)
        return SecretKeySpec(factory.generateSecret(spec).encoded, SECRET_KEY_SPEC_ALGORITHM)
    }

    private fun encrypt(plainText: String, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, generateIvSpec())

        return cipher.doFinal(plainText.toByteArray())
    }

    private fun decryption(cipherText: ByteArray, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, generateIvSpec())

        return String(cipher.doFinal(cipherText))
    }

    private fun generateIvSpec(): IvParameterSpec {
        return IvParameterSpec( ByteArray(16)
//            ByteArray(16).apply {
//                SecureRandom().nextBytes(this)
//            }
        )
    }
}
//
//fun main() {
//    val service = CryptoService()
//
//    val cipherText = service.fileEncryption(File(CryptoService::class.java.classLoader.getResource("file.json")!!.toURI()))
////
//    val nFile = File("users.json")
//    nFile.writeBytes(cipherText)
//    nFile.createNewFile()
////    println(service.fileDecryption(cipherText))
//
//}