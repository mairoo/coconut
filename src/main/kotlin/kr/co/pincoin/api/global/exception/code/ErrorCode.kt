package kr.co.pincoin.api.global.exception.code

import org.springframework.http.HttpStatus

interface ErrorCode {
    val status: HttpStatus
    val message: String
}