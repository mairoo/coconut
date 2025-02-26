package kr.co.pincoin.api.infra.common.jpa

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EntityListeners
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime

@Embeddable
@EntityListeners(AuditingEntityListener::class)
class DateTimeFields {
    @CreatedDate
    @Column(name = "created", updatable = false)
    var created: ZonedDateTime = ZonedDateTime.now()

    @LastModifiedDate
    @Column(name = "modified")
    var modified: ZonedDateTime = ZonedDateTime.now()
}
