package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

class User private constructor(
    val id: Long? = null,
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

    fun updateProfile(
        newFirstName: String? = null,
        newLastName: String? = null,
        newEmail: String? = null
    ): User {
        return copy(
            firstName = newFirstName ?: firstName,
            lastName = newLastName ?: lastName,
            email = newEmail ?: email
        )
    }

    fun updatePassword(newPassword: String): User {
        require(newPassword.isNotBlank()) { "비밀번호는 필수 입력값입니다" }
        return copy(password = newPassword)
    }

    fun updateLastLogin(loginTime: LocalDateTime): User {
        return copy(lastLogin = loginTime)
    }

    fun activate(): User {
        return copy(isActive = true)
    }

    fun deactivate(): User {
        return copy(isActive = false)
    }

    fun grantStaffPermission(): User {
        return copy(isStaff = true)
    }

    fun revokeStaffPermission(): User {
        return copy(isStaff = false)
    }

    fun grantSuperuserPermission(): User {
        return copy(isSuperuser = true)
    }

    fun revokeSuperuserPermission(): User {
        return copy(isSuperuser = false)
    }

    fun getFullName(): String {
        return when {
            firstName.isNotBlank() && lastName.isNotBlank() -> "$firstName $lastName"
            firstName.isNotBlank() -> firstName
            lastName.isNotBlank() -> lastName
            else -> username
        }
    }

    fun hasStaffPermission(): Boolean = isStaff || isSuperuser

    fun hasSuperuserPermission(): Boolean = isSuperuser

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
            id: Long? = null,
            password: String,
            lastLogin: LocalDateTime? = null,
            isSuperuser: Boolean = false,
            username: String,
            firstName: String = "",
            lastName: String = "",
            email: String,
            isStaff: Boolean = false,
            isActive: Boolean = true,
            dateJoined: LocalDateTime = LocalDateTime.now()
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
            dateJoined = dateJoined
        )

        fun create(
            username: String,
            password: String,
            email: String,
            firstName: String = "",
            lastName: String = "",
            isStaff: Boolean = false,
            isSuperuser: Boolean = false
        ): User = User(
            username = username,
            password = password,
            email = email,
            firstName = firstName,
            lastName = lastName,
            isStaff = isStaff,
            isSuperuser = isSuperuser
        )
    }
}