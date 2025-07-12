package kr.pincoin.api.infra.inventory.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields
import java.math.BigDecimal

@Entity
@Table(name = "shop_product")
class ProductEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),

    @Column(name = "name")
    val name: String,

    @Column(name = "subtitle")
    val subtitle: String,

    @Column(name = "code")
    val code: String,

    @Column(name = "list_price")
    val listPrice: BigDecimal,

    @Column(name = "selling_price")
    val sellingPrice: BigDecimal,

    @Column(name = "description")
    val description: String,

    @Column(name = "position")
    val position: Int,

    @Column(name = "status")
    val status: Int,

    @Column(name = "stock")
    val stock: Int,

    @Column(name = "category_id")
    val categoryId: Long,

    @Column(name = "store_id")
    val storeId: Long,

    @Column(name = "review_count")
    val reviewCount: Int,

    @Column(name = "naver_partner")
    val naverPartner: Boolean,

    @Column(name = "naver_partner_title")
    val naverPartnerTitle: String,

    @Column(name = "minimum_stock_level")
    val minimumStockLevel: Int,

    @Column(name = "pg")
    val pg: Boolean,

    @Column(name = "pg_selling_price")
    val pgSellingPrice: BigDecimal,

    @Column(name = "naver_attribute")
    val naverAttribute: String,

    @Column(name = "naver_partner_title_pg")
    val naverPartnerTitlePg: String,

    @Column(name = "review_count_pg")
    val reviewCountPg: Int,

    @Column(name = "maximum_stock_level")
    val maximumStockLevel: Int,

    @Column(name = "stock_quantity")
    val stockQuantity: Int,
) {
    companion object {
        fun of(
            id: Long? = null,
            name: String,
            subtitle: String = "",
            code: String,
            listPrice: BigDecimal,
            sellingPrice: BigDecimal,
            description: String = "",
            position: Int = 0,
            status: Int = 0,
            stock: Int = 0,
            categoryId: Long,
            storeId: Long,
            reviewCount: Int = 0,
            naverPartner: Boolean = false,
            naverPartnerTitle: String = "",
            minimumStockLevel: Int = 0,
            pg: Boolean = false,
            pgSellingPrice: BigDecimal = BigDecimal.ZERO,
            naverAttribute: String = "",
            naverPartnerTitlePg: String = "",
            reviewCountPg: Int = 0,
            maximumStockLevel: Int = 0,
            stockQuantity: Int = 0,
            isRemoved: Boolean = false,
        ) = ProductEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            name = name,
            subtitle = subtitle,
            code = code,
            listPrice = listPrice,
            sellingPrice = sellingPrice,
            description = description,
            position = position,
            status = status,
            stock = stock,
            categoryId = categoryId,
            storeId = storeId,
            reviewCount = reviewCount,
            naverPartner = naverPartner,
            naverPartnerTitle = naverPartnerTitle,
            minimumStockLevel = minimumStockLevel,
            pg = pg,
            pgSellingPrice = pgSellingPrice,
            naverAttribute = naverAttribute,
            naverPartnerTitlePg = naverPartnerTitlePg,
            reviewCountPg = reviewCountPg,
            maximumStockLevel = maximumStockLevel,
            stockQuantity = stockQuantity,
        )
    }
}