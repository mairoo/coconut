package kr.co.pincoin.api.infra.catalog.entity

import jakarta.persistence.*
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import java.math.BigDecimal

@Entity
@Table(name = "shop_category")
class CategoryEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "title")
    val title: String,

    @Column(name = "slug")
    val slug: String,

    @Column(name = "store_id")
    val storeId: Long,

    @Column(name = "thumbnail")
    val thumbnail: String = "",

    @Column(name = "description")
    val description: String = "",

    @Column(name = "description1")
    val description1: String = "",

    @Column(name = "discount_rate", precision = 3, scale = 2)
    val discountRate: BigDecimal,

    @Column(name = "pg")
    val pg: Boolean = false,

    @Column(name = "pg_discount_rate", precision = 3, scale = 2)
    val pgDiscountRate: BigDecimal = BigDecimal.ZERO,

    @Column(name = "naver_search_tag")
    val naverSearchTag: String = "",

    @Column(name = "naver_brand_name")
    val naverBrandName: String = "",

    @Column(name = "naver_maker_name")
    val naverMakerName: String = "",

    // 기존 django-mptt 호환 문제 (이후 삭제 필요)
    @Column(name = "lft")
    val lft: Int = 0,

    @Column(name = "rght")
    val rght: Int = 0,

    @Column(name = "tree_id")
    val treeId: Int = 0,

    @Column(name = "level")
    val level: Int = 0,

    @Column(name = "parentId")
    val parentId: Int? = null,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),
) {
    companion object {
        fun of(
            id: Long? = null,
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
        ) = CategoryEntity(
            id = id,
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