package kr.pincoin.api.infra.support.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "shop_shortmessageservice")
class ShortMessageEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Column(name = "created")
    val created: LocalDateTime,

    @Column(name = "modified")
    val modified: LocalDateTime,

    @Column(name = "phone_from")
    val phoneFrom: String?,

    @Column(name = "phone_to")
    val phoneTo: String?,

    @Column(name = "content")
    val content: String,

    @Column(name = "success")
    val success: Boolean,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime = LocalDateTime.now(),
            modified: LocalDateTime = LocalDateTime.now(),
            phoneFrom: String? = null,
            phoneTo: String? = null,
            content: String,
            success: Boolean = false,
        ) = ShortMessageEntity(
            id = id,
            created = created,
            modified = modified,
            phoneFrom = phoneFrom,
            phoneTo = phoneTo,
            content = content,
            success = success,
        )
    }
}