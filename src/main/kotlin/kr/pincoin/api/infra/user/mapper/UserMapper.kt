package kr.pincoin.api.infra.user.mapper

import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.infra.user.entity.UserEntity

fun UserEntity?.toModel(): User? =
    this?.let { entity ->
        User.of(
            id = entity.id,
            password = entity.password,
            lastLogin = entity.lastLogin,
            isSuperuser = entity.isSuperuser,
            username = entity.username,
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email,
            isStaff = entity.isStaff,
            isActive = entity.isActive,
            dateJoined = entity.dateJoined
        )
    }

fun List<UserEntity>?.toModelList(): List<User> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun User?.toEntity(): UserEntity? =
    this?.let { model ->
        UserEntity.of(
            id = model.id,
            password = model.password,
            lastLogin = model.lastLogin,
            isSuperuser = model.isSuperuser,
            username = model.username,
            firstName = model.firstName,
            lastName = model.lastName,
            email = model.email,
            isStaff = model.isStaff,
            isActive = model.isActive,
            dateJoined = model.dateJoined
        )
    }