package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*
import java.math.BigDecimal

data class CategoryCreateRequest(
    @field:NotBlank(message = "카테고리 제목은 필수 입력값입니다")
    @field:Size(min = 2, max = 100, message = "카테고리 제목은 2자 이상 100자 이하로 입력해주세요")
    @JsonProperty("title")
    val title: String,

    @field:NotBlank(message = "카테고리 슬러그는 필수 입력값입니다")
    @field:Pattern(
        regexp = "^[a-z0-9-]+$",
        message = "슬러그는 영문 소문자, 숫자, 하이픈(-)만 사용 가능합니다"
    )
    @field:Size(max = 100, message = "카테고리 슬러그는 최대 100자까지 입력 가능합니다")
    @JsonProperty("slug")
    val slug: String,

    @field:Size(max = 255, message = "썸네일 URL은 최대 255자까지 입력 가능합니다")
    @JsonProperty("thumbnail")
    val thumbnail: String = "",

    @field:Size(max = 1000, message = "설명은 최대 1000자까지 입력 가능합니다")
    @JsonProperty("description")
    val description: String = "",

    @field:Size(max = 5000, message = "상세 설명은 최대 5000자까지 입력 가능합니다")
    @JsonProperty("description1")
    val description1: String = "",

    @field:NotNull(message = "할인율은 필수 입력값입니다")
    @field:DecimalMin(value = "0.0", message = "할인율은 0 이상이어야 합니다")
    @field:DecimalMax(value = "100.0", message = "할인율은 100 이하여야 합니다")
    @JsonProperty("discountRate")
    val discountRate: BigDecimal,

    @JsonProperty("pg")
    val pg: Boolean = false,

    @field:DecimalMin(value = "0.0", message = "PG 할인율은 0 이상이어야 합니다")
    @field:DecimalMax(value = "100.0", message = "PG 할인율은 100 이하여야 합니다")
    @JsonProperty("pgDiscountRate")
    val pgDiscountRate: BigDecimal = BigDecimal.ZERO,

    @field:Size(max = 200, message = "네이버 검색 태그는 최대 200자까지 입력 가능합니다")
    @JsonProperty("naverSearchTag")
    val naverSearchTag: String = "",

    @field:Size(max = 100, message = "네이버 브랜드명은 최대 100자까지 입력 가능합니다")
    @JsonProperty("naverBrandName")
    val naverBrandName: String = "",

    @field:Size(max = 100, message = "네이버 제조사명은 최대 100자까지 입력 가능합니다")
    @JsonProperty("naverMakerName")
    val naverMakerName: String = ""
)