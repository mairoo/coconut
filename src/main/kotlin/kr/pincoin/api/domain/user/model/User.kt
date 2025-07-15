package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime
import java.util.*

class User private constructor(
    val id: Int? = null,
    val password: String,
    val lastLogin: LocalDateTime? = null,
    val isSuperuser: Boolean = false,
    val username: String,
    val firstName: String = "",
    val lastName: String = "",
    val email: String,
    val isStaff: Boolean = false,
    val isActive: Boolean = true,
    val dateJoined: LocalDateTime = LocalDateTime.now()
) {
    fun updatePassword(newPassword: String): User {
        require(newPassword.isNotBlank()) { "비밀번호는 필수 입력값입니다" }
        return copy(password = newPassword)
    }

    fun updateEmail(
        email: String,
    ): User =
        copy(email = email)

    fun updateFullName(
        firstName: String,
        lastName: String,
    ): User = copy(
        firstName = firstName,
        lastName = lastName,
    )

    fun updateStatus(
        isActive: Boolean,
    ): User =
        copy(isActive = isActive)

    /**
     * 소프트 삭제를 수행합니다.
     * 사용자 정보를 익명화한 후 비활성화합니다.
     */
    fun softDelete(): User {
        if (!isActive) return this

        return anonymize().copy(isActive = false)
    }

    /**
     * 사용자 정보를 익명화합니다.
     * 민감한 정보(username, email, password)를 난수로 암호화하고 권한을 제거합니다.
     */
    fun anonymize(): User {
        val randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8)

        return copy(
            username = "anonymous_user_$randomSuffix",
            email = "anonymous_$randomSuffix@anonymous.com",
            password = "anonymous_password_$randomSuffix",
            firstName = "",
            lastName = "",
            isSuperuser = false,
            isStaff = false,
        )
    }

    private fun copy(
        password: String = this.password,
        lastLogin: LocalDateTime? = this.lastLogin,
        isSuperuser: Boolean = this.isSuperuser,
        username: String = this.username,
        firstName: String = this.firstName,
        lastName: String = this.lastName,
        email: String = this.email,
        isStaff: Boolean = this.isStaff,
        isActive: Boolean = this.isActive,
        dateJoined: LocalDateTime = this.dateJoined
    ): User = User(
        id = this.id,
        password = password,
        lastLogin = lastLogin,
        isSuperuser = isSuperuser,
        username = username,
        firstName = firstName,
        lastName = lastName,
        email = email,
        isStaff = isStaff,
        isActive = isActive,
        dateJoined = dateJoined
    )

    companion object {
        fun of(
            id: Int? = null,
            password: String,
            lastLogin: LocalDateTime? = null,
            isSuperuser: Boolean = false,
            username: String,
            firstName: String = "",
            lastName: String = "",
            email: String,
            isStaff: Boolean = false,
            isActive: Boolean = true,
            dateJoined: LocalDateTime = LocalDateTime.now(),
        ): User = User(
            id = id,
            password = password,
            lastLogin = lastLogin,
            isSuperuser = isSuperuser,
            username = username,
            firstName = firstName,
            lastName = lastName,
            email = email,
            isStaff = isStaff,
            isActive = isActive,
            dateJoined = dateJoined,
        )
    }
}