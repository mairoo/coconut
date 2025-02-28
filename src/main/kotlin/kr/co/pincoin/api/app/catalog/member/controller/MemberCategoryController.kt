package kr.co.pincoin.api.app.catalog.member.controller

import kr.co.pincoin.api.app.catalog.member.service.MemberCategoryService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categories")
class MemberCategoryController(
    private val memberCategoryService: MemberCategoryService,
) {
}