package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import java.math.BigDecimal

data class ProductCreateRequest(
    @field:NotBlank(message = "상품명은 필수 입력값입니다")
    @field:Size(min = 2, max = 200, message = "상품명은 2자 이상 200자 이하로 입력해주세요")
    @JsonProperty("name")
    val name: String,

    @field:Size(max = 300, message = "부제목은 최대 300자까지 입력 가능합니다")
    @JsonProperty("subtitle")
    val subtitle: String = "",

    @field:NotBlank(message = "상품 코드는 필수 입력값입니다")
    @field:Pattern(
        regexp = "^[A-Za-z0-9-_]+$",
        message = "상품 코드는 영문, 숫자, 하이픈(-), 밑줄(_)만 사용 가능합니다"
    )
    @field:Size(max = 50, message = "상품 코드는 최대 50자까지 입력 가능합니다")
    @JsonProperty("code")
    val code: String,

    @field:NotNull(message = "카테고리 ID는 필수 입력값입니다")
    @field:Positive(message = "카테고리 ID는 양수여야 합니다")
    @JsonProperty("categoryId")
    val categoryId: Long,

    @field:NotNull(message = "정가는 필수 입력값입니다")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "정가는 0보다 커야 합니다")
    @JsonProperty("listPrice")
    val listPrice: BigDecimal,

    @field:NotNull(message = "판매가는 필수 입력값입니다")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "판매가는 0보다 커야 합니다")
    @JsonProperty("sellingPrice")
    val sellingPrice: BigDecimal,

    @JsonProperty("pg")
    val pg: Boolean = false,

    @JsonProperty("pgSellingPrice")
    val pgSellingPrice: BigDecimal,

    @field:Size(max = 5000, message = "상품 설명은 최대 5000자까지 입력 가능합니다")
    @JsonProperty("description")
    val description: String = "",

    @field:NotNull(message = "상품 위치는 필수 입력값입니다")
    @field:Min(value = 0, message = "상품 위치는 0 이상이어야 합니다")
    @JsonProperty("position")
    val position: Int,

    @JsonProperty("status")
    val status: ProductStatus = ProductStatus.ENABLED,

    @field:Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
    @JsonProperty("stockQuantity")
    val stockQuantity: Int = 0,

    @JsonProperty("stock")
    val stock: ProductStock = ProductStock.IN_STOCK,

    @field:Min(value = 0, message = "최소 재고 수량은 0 이상이어야 합니다")
    @JsonProperty("minimumStockLevel")
    val minimumStockLevel: Int = 0,

    @field:Min(value = 0, message = "최대 재고 수량은 0 이상이어야 합니다")
    @JsonProperty("maximumStockLevel")
    val maximumStockLevel: Int = 0,

    @JsonProperty("naverPartner")
    val naverPartner: Boolean = false,

    @field:Size(max = 200, message = "네이버 파트너 제목은 최대 200자까지 입력 가능합니다")
    @JsonProperty("naverPartnerTitle")
    val naverPartnerTitle: String = "",

    @field:Size(max = 200, message = "네이버 파트너 PG 제목은 최대 200자까지 입력 가능합니다")
    @JsonProperty("naverPartnerTitlePg")
    val naverPartnerTitlePg: String = "",

    @field:Size(max = 500, message = "네이버 속성은 최대 500자까지 입력 가능합니다")
    @JsonProperty("naverAttribute")
    val naverAttribute: String = ""
)