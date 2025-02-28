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
    fun updateBasicInfo(
        newTitle: String? = null,
        newSlug: String? = null
    ): Category = copy(
        title = newTitle ?: title,
        slug = newSlug ?: slug
    )

    fun updateDescriptions(
        newDescription: String? = null,
        newDescription1: String? = null
    ): Category = copy(
        description = newDescription ?: description,
        description1 = newDescription1 ?: description1
    )

    fun updateThumbnail(
        newThumbnail: String,
    ): Category = copy(thumbnail = newThumbnail)

    fun updatePriceInfo(
        newDiscountRate: BigDecimal? = null,
        newPgDiscountRate: BigDecimal? = null
    ): Category = copy(
        discountRate = newDiscountRate ?: discountRate,
        pgDiscountRate = newPgDiscountRate ?: pgDiscountRate
    )

    fun updatePgStatus(newPg: Boolean? = null): Category = copy(
        pg = newPg ?: pg
    )

    fun updateNaverInfo(
        newNaverSearchTag: String? = null,
        newNaverBrandName: String? = null,
        newNaverMakerName: String? = null
    ): Category = copy(
        naverSearchTag = newNaverSearchTag ?: naverSearchTag,
        naverBrandName = newNaverBrandName ?: naverBrandName,
        naverMakerName = newNaverMakerName ?: naverMakerName
    )

    private fun copy(
        title: String? = null,
        slug: String? = null,
        thumbnail: String? = null,
        description: String? = null,
        description1: String? = null,
        discountRate: BigDecimal? = null,
        pg: Boolean? = null,
        pgDiscountRate: BigDecimal? = null,
        naverSearchTag: String? = null,
        naverBrandName: String? = null,
        naverMakerName: String? = null,
    ): Category = Category(
        id = this.id,
        created = this.created,
        modified = this.modified,
        title = title ?: this.title,
        slug = slug ?: this.slug,
        thumbnail = thumbnail ?: this.thumbnail,
        description = description ?: this.description,
        description1 = description1 ?: this.description1,
        discountRate = discountRate ?: this.discountRate,
        pg = pg ?: this.pg,
        pgDiscountRate = pgDiscountRate ?: this.pgDiscountRate,
        naverSearchTag = naverSearchTag ?: this.naverSearchTag,
        naverBrandName = naverBrandName ?: this.naverBrandName,
        naverMakerName = naverMakerName ?: this.naverMakerName
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