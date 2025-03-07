package kr.co.pincoin.api.infra.user.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "auth_user")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "password")
    val password: String,

    @Column(name = "last_login")
    val lastLogin: ZonedDateTime? = null,

    @Column(name = "is_superuser")
    val isSuperuser: Boolean = false,

    @Column(name = "username")
    val username: String,

    @Column(name = "first_name")
    val firstName: String = "",

    @Column(name = "last_name")
    val lastName: String = "",

    @Column(name = "email")
    val email: String = "",

    @Column(name = "is_staff")
    val isStaff: Boolean = false,

    @Column(name = "is_active")
    val isActive: Boolean = true,

    @Column(name = "date_joined")
    val dateJoined: ZonedDateTime = ZonedDateTime.now()
) {
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
        ) = UserEntity(
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