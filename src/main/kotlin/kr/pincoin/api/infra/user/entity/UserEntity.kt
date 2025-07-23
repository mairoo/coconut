package kr.pincoin.api.infra.user.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "auth_user")
class UserEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int?,

    @Column(name = "password")
    val password: String,

    @Column(name = "last_login")
    val lastLogin: LocalDateTime?,

    @Column(name = "is_superuser")
    val isSuperuser: Boolean,

    @Column(name = "username")
    val username: String,

    @Column(name = "first_name")
    val firstName: String,

    @Column(name = "last_name")
    val lastName: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "is_staff")
    val isStaff: Boolean,

    @Column(name = "is_active")
    val isActive: Boolean,

    @Column(name = "date_joined")
    val dateJoined: LocalDateTime,

    @Column(name = "keycloak_id")
    val keycloakId: UUID? = null
) {
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
            keycloakId: UUID? = null,
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
            dateJoined = dateJoined,
            keycloakId = keycloakId,
        )
    }
}