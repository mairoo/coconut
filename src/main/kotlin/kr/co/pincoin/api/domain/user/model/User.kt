package kr.co.pincoin.api.domain.user.model

import java.time.ZonedDateTime

class User private constructor(
    // 1. 공통 불변 필드
    val id: Int? = null,
    val dateJoined: ZonedDateTime,

    // 2. 도메인 로직 불변 필드
    val username: String,

    // 3. 도메인 로직 가변 필드
    val password: String,
    val lastLogin: ZonedDateTime?,
    val isSuperuser: Boolean,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isStaff: Boolean,
    val isActive: Boolean,
) {
    fun updatePassword(newPassword: String): User =
        copy(password = newPassword)

    fun updateLastLogin(newLastLogin: ZonedDateTime?): User =
        copy(lastLogin = newLastLogin)

    fun updateSuperuser(newIsSuperuser: Boolean): User =
        copy(isSuperuser = newIsSuperuser)

    fun updateName(newFirstName: String? = null, newLastName: String? = null): User =
        copy(
            firstName = newFirstName ?: firstName,
            lastName = newLastName ?: lastName
        )

    fun updateEmail(newEmail: String): User =
        copy(email = newEmail)

    fun updateStaff(newIsStaff: Boolean): User =
        copy(isStaff = newIsStaff)

    fun updateActive(newIsActive: Boolean): User =
        copy(isActive = newIsActive)

    private fun copy(
        password: String = this.password,
        lastLogin: ZonedDateTime? = this.lastLogin,
        isSuperuser: Boolean = this.isSuperuser,
        firstName: String = this.firstName,
        lastName: String = this.lastName,
        email: String = this.email,
        isStaff: Boolean = this.isStaff,
        isActive: Boolean = this.isActive
    ): User = User(
        id = this.id,
        dateJoined = this.dateJoined,
        username = this.username,
        password = password,
        lastLogin = lastLogin,
        isSuperuser = isSuperuser,
        firstName = firstName,
        lastName = lastName,
        email = email,
        isStaff = isStaff,
        isActive = isActive
    )

    companion object {
        fun of(
            id: Int? = null,
            password: String,
            lastLogin: ZonedDateTime? = null,
            isSuperuser: Boolean = false,
            username: String,
            firstName: String = "",
            lastName: String = "",
            email: String = "",
            isStaff: Boolean = false,
            isActive: Boolean = true,
            dateJoined: ZonedDateTime = ZonedDateTime.now()
        ): User =
            User(
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