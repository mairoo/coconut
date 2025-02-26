package kr.co.pincoin.api.infra.catalog.entity

import jakarta.persistence.*
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import kr.co.pincoin.api.infra.catalog.converter.ProductStatusConverter
import kr.co.pincoin.api.infra.catalog.converter.ProductStockConverter
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import kr.co.pincoin.api.infra.common.jpa.RemovalFields
import java.math.BigDecimal

@Entity
@Table(name = "shop_product")
class ProductEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "name")
    val name: String,

    @Column(name = "subtitle")
    val subtitle: String = "",

    @Column(name = "code", unique = true)
    val code: String,

    @Column(name = "list_price", precision = 11, scale = 2)
    val listPrice: BigDecimal,

    @Column(name = "selling_price", precision = 11, scale = 2)
    val sellingPrice: BigDecimal,

    @Column(name = "pg")
    val pg: Boolean = false,

    @Column(name = "pg_selling_price", precision = 11, scale = 2)
    val pgSellingPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "description")
    val description: String = "",

    @Column(name = "store_id")
    val storeId: Long,

    @Column(name = "category_id")
    val categoryId: Long,

    @Column(name = "position")
    val position: Int,

    @Column(name = "status")
    @Convert(converter = ProductStatusConverter::class)
    val status: ProductStatus,

    @Column(name = "stock_quantity")
    val stockQuantity: Int = 0,

    @Column(name = "stock")
    @Convert(converter = ProductStockConverter::class)
    val stock: ProductStock,

    @Column(name = "minimum_stock_level")
    val minimumStockLevel: Int = 0,

    @Column(name = "maximum_stock_level")
    val maximumStockLevel: Int = 0,

    @Column(name = "review_count")
    val reviewCount: Int = 0,

    @Column(name = "review_count_pg")
    val reviewCountPg: Int = 0,

    @Column(name = "naver_partner")
    val naverPartner: Boolean = false,

    @Column(name = "naver_partner_title")
    val naverPartnerTitle: String = "",

    @Column(name = "naver_partner_title_pg")
    val naverPartnerTitlePg: String = "",

    @Column(name = "naver_attribute")
    val naverAttribute: String = "",

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),
) {
    companion object {
        fun of(
            id: Long? = null,
            isRemoved: Boolean = false,
            name: String,
            subtitle: String = "",
            code: String,
            listPrice: BigDecimal,
            sellingPrice: BigDecimal,
            pg: Boolean = false,
            pgSellingPrice: BigDecimal = BigDecimal.ZERO,
            description: String = "",
            storeId: Long,
            categoryId: Long,
            position: Int,
            status: ProductStatus = ProductStatus.ENABLED,
            stockQuantity: Int = 0,
            stock: ProductStock = ProductStock.IN_STOCK,
            minimumStockLevel: Int = 0,
            maximumStockLevel: Int = 0,
            reviewCount: Int = 0,
            reviewCountPg: Int = 0,
            naverPartner: Boolean = false,
            naverPartnerTitle: String = "",
            naverPartnerTitlePg: String = "",
            naverAttribute: String = "",
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
            pg = pg,
            pgSellingPrice = pgSellingPrice,
            description = description,
            storeId = storeId,
            categoryId = categoryId,
            position = position,
            status = status,
            stockQuantity = stockQuantity,
            stock = stock,
            minimumStockLevel = minimumStockLevel,
            maximumStockLevel = maximumStockLevel,
            reviewCount = reviewCount,
            reviewCountPg = reviewCountPg,
            naverPartner = naverPartner,
            naverPartnerTitle = naverPartnerTitle,
            naverPartnerTitlePg = naverPartnerTitlePg,
            naverAttribute = naverAttribute,
            dateTimeFields = DateTimeFields(),
        )
    }
}