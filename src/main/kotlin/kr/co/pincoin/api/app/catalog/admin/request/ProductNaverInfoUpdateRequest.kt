package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size

data class ProductNaverInfoUpdateRequest(
    @JsonProperty("naverPartner")
    val naverPartner: Boolean? = null,

    @field:Size(max = 200, message = "네이버 파트너 제목은 최대 200자까지 입력 가능합니다")
    @JsonProperty("naverPartnerTitle")
    val naverPartnerTitle: String? = null,

    @field:Size(max = 200, message = "네이버 파트너 PG 제목은 최대 200자까지 입력 가능합니다")
    @JsonProperty("naverPartnerTitlePg")
    val naverPartnerTitlePg: String? = null,

    @field:Size(max = 500, message = "네이버 속성은 최대 500자까지 입력 가능합니다")
    @JsonProperty("naverAttribute")
    val naverAttribute: String? = null
)