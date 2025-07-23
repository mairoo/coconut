package kr.pincoin.api.infra.inventory.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import java.math.BigDecimal

@Entity
@Table(name = "shop_category")
class CategoryEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Column(name = "title")
    val title: String,

    @Column(name = "slug")
    val slug: String,

    @Column(name = "thumbnail")
    val thumbnail: String,

    @Column(name = "description")
    val description: String,

    @Column(name = "description1")
    val description1: String,

    @Column(name = "lft")
    val lft: Int,

    @Column(name = "rght")
    val rght: Int,

    @Column(name = "tree_id")
    val treeId: Int,

    @Column(name = "level")
    val level: Int,

    @Column(name = "parent_id")
    val parentId: Long?,

    @Column(name = "store_id")
    val storeId: Long,

    @Column(name = "discount_rate")
    val discountRate: BigDecimal,

    @Column(name = "pg")
    val pg: Boolean,

    @Column(name = "pg_discount_rate")
    val pgDiscountRate: BigDecimal,

    @Column(name = "naver_search_tag")
    val naverSearchTag: String,

    @Column(name = "naver_brand_name")
    val naverBrandName: String,

    @Column(name = "naver_maker_name")
    val naverMakerName: String,
) {
    companion object {
        fun of(
            id: Long? = null,
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
        ) = CategoryEntity(
            id = id,
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