package kr.co.pincoin.api.app.catalog.member.controller

import kr.co.pincoin.api.app.catalog.member.service.MemberProductService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class MemberProductController(
    private val memberProductService: MemberProductService,
) {
}