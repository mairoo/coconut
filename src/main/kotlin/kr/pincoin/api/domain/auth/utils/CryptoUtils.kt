package kr.pincoin.api.domain.auth.utils

import kr.pincoin.api.domain.auth.properties.AuthProperties
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Component
class CryptoUtils(
    private val authProperties: AuthProperties,
) {
    private val algorithm = "AES"

    fun encrypt(
        plainText: String,
    ): String {
        val cipher = Cipher.getInstance(algorithm)
        val keySpec = SecretKeySpec(authProperties.crypto.secretKey.toByteArray(), algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encrypted = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    fun decrypt(
        encryptedText: String,
    ): String {
        val cipher = Cipher.getInstance(algorithm)
        val keySpec = SecretKeySpec(authProperties.crypto.secretKey.toByteArray(), algorithm)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val decoded = Base64.getDecoder().decode(encryptedText)
        val decrypted = cipher.doFinal(decoded)
        return String(decrypted)
    }
}
