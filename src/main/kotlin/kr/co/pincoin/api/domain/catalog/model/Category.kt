package kr.co.pincoin.api.domain.catalog.model

import java.math.BigDecimal
import java.time.ZonedDateTime

class Category private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 도메인 로직 불변 필드
    val storeId: Long,

    // 3. 도메인 로직 가변 필드
    title: String,
    slug: String,
    thumbnail: String,
    description: String,
    description1: String,
    discountRate: BigDecimal,
    pg: Boolean,
    pgDiscountRate: BigDecimal,
    naverSearchTag: String,
    naverBrandName: String,
    naverMakerName: String,
) {
    var title: String = title
        private set

    var slug: String = slug
        private set

    var thumbnail: String = thumbnail
        private set

    var description: String = description
        private set

    var description1: String = description1
        private set

    var discountRate: BigDecimal = discountRate
        private set

    var pg: Boolean = pg
        private set

    var pgDiscountRate: BigDecimal = pgDiscountRate
        private set

    var naverSearchTag: String = naverSearchTag
        private set

    var naverBrandName: String = naverBrandName
        private set

    var naverMakerName: String = naverMakerName
        private set

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            title: String,
            slug: String,
            storeId: Long,
            thumbnail: String = "",
            description: String = "",
            description1: String = "",
            discountRate: BigDecimal,
            pg: Boolean = false,
            pgDiscountRate: BigDecimal = BigDecimal.ZERO,
            naverSearchTag: String = "",
            naverBrandName: String = "",
            naverMakerName: String = "",
        ): Category =
            Category(
                id = id,
                created = created,
                modified = modified,
                title = title,
                slug = slug,
                storeId = storeId,
                thumbnail = thumbnail,
                description = description,
                description1 = description1,
                discountRate = discountRate,
                pg = pg,
                pgDiscountRate = pgDiscountRate,
                naverSearchTag = naverSearchTag,
                naverBrandName = naverBrandName,
                naverMakerName = naverMakerName,
            )
    }
}