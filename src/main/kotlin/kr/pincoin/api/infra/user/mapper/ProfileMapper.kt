package kr.pincoin.api.infra.user.mapper

import kr.pincoin.api.domain.user.model.Profile
import kr.pincoin.api.infra.user.entity.ProfileEntity

fun ProfileEntity?.toModel(): Profile? =
    this?.let { entity ->
        Profile.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            phone = entity.phone,
            address = entity.address,
            phoneVerified = entity.phoneVerified,
            documentVerified = entity.documentVerified,
            photoId = entity.photoId,
            card = entity.card,
            totalOrderCount = entity.totalOrderCount,
            lastPurchased = entity.lastPurchased,
            maxPrice = entity.maxPrice,
            averagePrice = entity.averagePrice,
            userId = entity.userId,
            memo = entity.memo,
            phoneVerifiedStatus = entity.phoneVerifiedStatus,
            dateOfBirth = entity.dateOfBirth,
            firstPurchased = entity.firstPurchased,
            totalListPrice = entity.totalListPrice,
            totalSellingPrice = entity.totalSellingPrice,
            domestic = entity.domestic,
            gender = entity.gender,
            telecom = entity.telecom,
            notPurchasedMonths = entity.notPurchasedMonths,
            repurchased = entity.repurchased,
            mileage = entity.mileage,
            allowOrder = entity.allowOrder,
        )
    }

fun List<ProfileEntity>?.toModelList(): List<Profile> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun Profile?.toEntity(): ProfileEntity? =
    this?.let { model ->
        ProfileEntity.of(
            id = model.id,
            phone = model.phone,
            address = model.address,
            phoneVerified = model.phoneVerified,
            documentVerified = model.documentVerified,
            photoId = model.photoId,
            card = model.card,
            totalOrderCount = model.totalOrderCount,
            lastPurchased = model.lastPurchased,
            maxPrice = model.maxPrice,
            averagePrice = model.averagePrice,
            userId = model.userId,
            memo = model.memo,
            phoneVerifiedStatus = model.phoneVerifiedStatus,
            dateOfBirth = model.dateOfBirth,
            firstPurchased = model.firstPurchased,
            totalListPrice = model.totalListPrice,
            totalSellingPrice = model.totalSellingPrice,
            domestic = model.domestic,
            gender = model.gender,
            telecom = model.telecom,
            notPurchasedMonths = model.notPurchasedMonths,
            repurchased = model.repurchased,
            mileage = model.mileage,
            allowOrder = model.allowOrder
        )
    }