package kr.pincoin.api.global.security.encoder

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class DjangoPasswordEncoder : PasswordEncoder {
    private val log = KotlinLogging.logger {}

    companion object {
        private const val ALGORITHM = "pbkdf2_sha256"
        private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val DEFAULT_ITERATIONS = 1200000
        private const val SALT_LENGTH = 16
        private const val HASH_LENGTH = 32
        private const val BITS_PER_BYTE = 8
        private const val PASSWORD_HASH_FORMAT = "%s$%d$%s$%s"
        private val secureRandom = SecureRandom()

        private fun generateSalt(length: Int = SALT_LENGTH): String {
            val saltBytes = ByteArray(length).apply {
                secureRandom.nextBytes(this)
            }
            return Base64.getUrlEncoder().withoutPadding().encodeToString(saltBytes)
        }
    }

    private fun encrypt(
        plain: String,
        salt: String,
        iterations: Int
    ): String = try {
        val keySpec = PBEKeySpec(
            plain.toCharArray(),
            salt.toByteArray(),
            iterations,
            HASH_LENGTH * BITS_PER_BYTE
        )

        val secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        val secret = secretKeyFactory.generateSecret(keySpec)
        Base64.getEncoder().encodeToString(secret.encoded)
    } catch (e: Exception) {
        log.error(e) { "Password encryption failed" }
        throw IllegalStateException("Password encryption error", e)
    }

    private fun encodePassword(
        plain: String,
        salt: String = generateSalt(),
        iterations: Int = DEFAULT_ITERATIONS
    ): String {
        val hash = encrypt(plain, salt, iterations)
        return String.format(PASSWORD_HASH_FORMAT, ALGORITHM, iterations, salt, hash)
    }

    override fun encode(rawPassword: CharSequence): String =
        encodePassword(rawPassword.toString())

    override fun matches(
        rawPassword: CharSequence,
        encodedPassword: String
    ): Boolean = try {
        val parts = encodedPassword.split("$")

        // 섹션 검증
        require(parts.size == 4) { "Invalid password hash format" }

        val (algorithm, iterations, salt, _) = parts

        // 알고리즘과 반복 횟수 검증
        require(algorithm == ALGORITHM) { "Unsupported algorithm" }

        val regeneratedHash = encodePassword(
            rawPassword.toString(),
            salt,
            iterations.toInt()
        )

        regeneratedHash == encodedPassword
    } catch (e: Exception) {
        log.error(e) { "Password match validation failed" }
        false
    }
}