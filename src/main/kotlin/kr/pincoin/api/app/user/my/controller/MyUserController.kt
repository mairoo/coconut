package kr.pincoin.api.app.user.my.controller

import kr.pincoin.api.app.user.my.service.MyUserService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member/users")
class MyUserController(
    private val myUserService: MyUserService,
) {
}