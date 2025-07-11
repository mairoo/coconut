package kr.pincoin.api.app.monitoring

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {

    @GetMapping("/health", produces = ["text/plain"])
    fun health(): String {
        return "OK"
    }
}