package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

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
    fun updateProfile(
        newFirstName: String? = null,
        newLastName: String? = null,
        newEmail: String? = null
    ): User =
        copy(
            firstName = newFirstName ?: firstName,
            lastName = newLastName ?: lastName,
            email = newEmail ?: email
        )

    fun updatePassword(newPassword: String): User {
        require(newPassword.isNotBlank()) { "비밀번호는 필수 입력값입니다" }
        return copy(password = newPassword)
    }

    fun updateLastLogin(loginTime: LocalDateTime): User =
        copy(lastLogin = loginTime)

    fun activate(): User =
        copy(isActive = true)

    fun deactivate(): User =
        copy(isActive = false)

    fun grantStaffPermission(): User =
        copy(isStaff = true)

    fun revokeStaffPermission(): User =
        copy(isStaff = false)

    fun grantSuperuserPermission(): User =
        copy(isSuperuser = true)

    fun revokeSuperuserPermission(): User =
        copy(isSuperuser = false)

    fun getFullName(): String =
        when {
            firstName.isNotBlank() && lastName.isNotBlank() -> "$firstName $lastName"
            firstName.isNotBlank() -> firstName
            lastName.isNotBlank() -> lastName
            else -> username
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
    }
}