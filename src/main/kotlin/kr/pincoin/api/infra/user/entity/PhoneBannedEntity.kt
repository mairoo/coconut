package kr.pincoin.api.infra.user.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "member_phonebanned")
class PhoneBannedEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Column(name = "created")
    val created: LocalDateTime,

    @Column(name = "modified")
    val modified: LocalDateTime,

    @Column(name = "is_removed")
    val isRemoved: Boolean,

    @Column(name = "phone")
    val phone: String,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime = LocalDateTime.now(),
            modified: LocalDateTime = LocalDateTime.now(),
            isRemoved: Boolean = false,
            phone: String
        ) = PhoneBannedEntity(
            id = id,
            created = created,
            modified = modified,
            isRemoved = isRemoved,
            phone = phone
        )
    }
}