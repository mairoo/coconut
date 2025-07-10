package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.UserEntity

interface UserQueryRepository {
    fun findById(id: Long): UserEntity?
}