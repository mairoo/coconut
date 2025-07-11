package kr.pincoin.api.domain.inventory.model

import java.math.BigDecimal
import java.time.LocalDateTime

class Category private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val title: String,
    val slug: String,
    val thumbnail: String = "",
    val description: String = "",
    val description1: String = "",
    val lft: Int,
    val rght: Int,
    val treeId: Int,
    val level: Int,
    val parentId: Long? = null,
    val storeId: Long,
    val discountRate: BigDecimal = BigDecimal.ZERO,
    val pg: Boolean = false,
    val pgDiscountRate: BigDecimal = BigDecimal.ZERO,
    val naverSearchTag: String = "",
    val naverBrandName: String = "",
    val naverMakerName: String = "",
) {
    private fun copy(
        title: String = this.title,
        slug: String = this.slug,
        thumbnail: String = this.thumbnail,
        description: String = this.description,
        description1: String = this.description1,
        lft: Int = this.lft,
        rght: Int = this.rght,
        treeId: Int = this.treeId,
        level: Int = this.level,
        parentId: Long? = this.parentId,
        storeId: Long = this.storeId,
        discountRate: BigDecimal = this.discountRate,
        pg: Boolean = this.pg,
        pgDiscountRate: BigDecimal = this.pgDiscountRate,
        naverSearchTag: String = this.naverSearchTag,
        naverBrandName: String = this.naverBrandName,
        naverMakerName: String = this.naverMakerName,
    ): Category = Category(
        id = this.id,
        created = this.created,
        modified = this.modified,
        title = title,
        slug = slug,
        thumbnail = thumbnail,
        description = description,
        description1 = description1,
        lft = lft,
        rght = rght,
        treeId = treeId,
        level = level,
        parentId = parentId,
        storeId = storeId,
        discountRate = discountRate,
        pg = pg,
        pgDiscountRate = pgDiscountRate,
        naverSearchTag = naverSearchTag,
        naverBrandName = naverBrandName,
        naverMakerName = naverMakerName,
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            title: String,
            slug: String,
            thumbnail: String = "",
            description: String = "",
            description1: String = "",
            lft: Int,
            rght: Int,
            treeId: Int,
            level: Int,
            parentId: Long? = null,
            storeId: Long,
            discountRate: BigDecimal = BigDecimal.ZERO,
            pg: Boolean = false,
            pgDiscountRate: BigDecimal = BigDecimal.ZERO,
            naverSearchTag: String = "",
            naverBrandName: String = "",
            naverMakerName: String = "",
        ): Category = Category(
            id = id,
            created = created,
            modified = modified,
            title = title,
            slug = slug,
            thumbnail = thumbnail,
            description = description,
            description1 = description1,
            lft = lft,
            rght = rght,
            treeId = treeId,
            level = level,
            parentId = parentId,
            storeId = storeId,
            discountRate = discountRate,
            pg = pg,
            pgDiscountRate = pgDiscountRate,
            naverSearchTag = naverSearchTag,
            naverBrandName = naverBrandName,
            naverMakerName = naverMakerName,
        )
    }
}