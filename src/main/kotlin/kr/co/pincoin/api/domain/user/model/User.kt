package kr.co.pincoin.api.domain.user.model

import java.time.ZonedDateTime

class User private constructor(
    // 1. 공통 불변 필드
    val id: Int? = null,
    val dateJoined: ZonedDateTime,

    // 2. 도메인 로직 불변 필드
    val username: String,

    // 3. 도메인 로직 가변 필드
    password: String,
    lastLogin: ZonedDateTime?,
    isSuperuser: Boolean,
    firstName: String,
    lastName: String,
    email: String,
    isStaff: Boolean,
    isActive: Boolean,
) {
    var password: String = password
        private set

    var lastLogin: ZonedDateTime? = lastLogin
        private set

    var isSuperuser: Boolean = isSuperuser
        private set

    var firstName: String = firstName
        private set

    var lastName: String = lastName
        private set

    var email: String = email
        private set

    var isStaff: Boolean = isStaff
        private set

    var isActive: Boolean = isActive
        private set

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