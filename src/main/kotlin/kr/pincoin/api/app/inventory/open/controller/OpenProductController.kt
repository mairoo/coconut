package kr.pincoin.api.app.inventory.open.controller

import kr.pincoin.api.app.inventory.open.service.OpenProductService
import org.springframework.web.bind.annotation.RestController

@RestController
class OpenProductController(
    private val openProductService: OpenProductService,
) {
}