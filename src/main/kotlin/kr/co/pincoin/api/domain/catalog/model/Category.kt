package kr.co.pincoin.api.domain.catalog.model

import java.math.BigDecimal
import java.time.ZonedDateTime

class Category private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 도메인 로직 불변 필드
    // 3. 도메인 로직 가변 필드
    val title: String,
    val slug: String,
    val thumbnail: String,
    val description: String,
    val description1: String,
    val discountRate: BigDecimal,
    val pg: Boolean,
    val pgDiscountRate: BigDecimal,
    val naverSearchTag: String,
    val naverBrandName: String,
    val naverMakerName: String,
) {
    fun changeBasicInfo(
        newTitle: String? = null,
        newSlug: String? = null
    ): Category = copy(
        title = newTitle ?: title,
        slug = newSlug ?: slug
    )

    fun changeDescriptions(
        newThumbnail: String? = null,
        newDescription: String? = null,
        newDescription1: String? = null
    ): Category = copy(
        thumbnail = newThumbnail ?: thumbnail,
        description = newDescription ?: description,
        description1 = newDescription1 ?: description1
    )

    fun changePriceInfo(
        newDiscountRate: BigDecimal? = null,
        newPgDiscountRate: BigDecimal? = null
    ): Category = copy(
        discountRate = newDiscountRate ?: discountRate,
        pgDiscountRate = newPgDiscountRate ?: pgDiscountRate
    )

    fun changePgStatus(newPg: Boolean? = null): Category = copy(
        pg = newPg ?: pg
    )

    fun changeNaverInfo(
        newNaverSearchTag: String? = null,
        newNaverBrandName: String? = null,
        newNaverMakerName: String? = null
    ): Category = copy(
        naverSearchTag = newNaverSearchTag ?: naverSearchTag,
        naverBrandName = newNaverBrandName ?: naverBrandName,
        naverMakerName = newNaverMakerName ?: naverMakerName
    )

    private fun copy(
        title: String = this.title,
        slug: String = this.slug,
        thumbnail: String = this.thumbnail,
        description: String = this.description,
        description1: String = this.description1,
        discountRate: BigDecimal = this.discountRate,
        pg: Boolean = this.pg,
        pgDiscountRate: BigDecimal = this.pgDiscountRate,
        naverSearchTag: String = this.naverSearchTag,
        naverBrandName: String = this.naverBrandName,
        naverMakerName: String = this.naverMakerName
    ): Category = Category(
        id = this.id,
        created = this.created,
        modified = this.modified,
        title = title,
        slug = slug,
        thumbnail = thumbnail,
        description = description,
        description1 = description1,
        discountRate = discountRate,
        pg = pg,
        pgDiscountRate = pgDiscountRate,
        naverSearchTag = naverSearchTag,
        naverBrandName = naverBrandName,
        naverMakerName = naverMakerName
    )

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            title: String,
            slug: String,
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