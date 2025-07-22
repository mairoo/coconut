package kr.pincoin.api.infra.user.mapper

import kr.pincoin.api.domain.user.model.Profile
import kr.pincoin.api.infra.user.entity.ProfileEntity

fun ProfileEntity?.toModel(): Profile? =
    this?.let { entity ->
        Profile.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            userId = entity.userId,
            address = entity.address,
            phone = entity.phone,
            phoneVerified = entity.phoneVerified,
            phoneVerifiedStatus = entity.phoneVerifiedStatus,
            dateOfBirth = entity.dateOfBirth,
            domestic = entity.domestic,
            gender = entity.gender,
            telecom = entity.telecom,
            photoId = entity.photoId,
            card = entity.card,
            documentVerified = entity.documentVerified,
            totalOrderCount = entity.totalOrderCount,
            firstPurchased = entity.firstPurchased,
            lastPurchased = entity.lastPurchased,
            maxPrice = entity.maxPrice,
            averagePrice = entity.averagePrice,
            totalListPrice = entity.totalListPrice,
            totalSellingPrice = entity.totalSellingPrice,
            notPurchasedMonths = entity.notPurchasedMonths,
            repurchased = entity.repurchased,
            memo = entity.memo,
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
            userId = model.userId,
            address = model.address,
            phone = model.phone,
            phoneVerified = model.phoneVerified,
            phoneVerifiedStatus = model.phoneVerifiedStatus,
            dateOfBirth = model.dateOfBirth,
            domestic = model.domestic,
            gender = model.gender,
            telecom = model.telecom,
            photoId = model.photoId,
            card = model.card,
            documentVerified = model.documentVerified,
            totalOrderCount = model.totalOrderCount,
            firstPurchased = model.firstPurchased,
            lastPurchased = model.lastPurchased,
            maxPrice = model.maxPrice,
            averagePrice = model.averagePrice,
            totalListPrice = model.totalListPrice,
            totalSellingPrice = model.totalSellingPrice,
            notPurchasedMonths = model.notPurchasedMonths,
            repurchased = model.repurchased,
            memo = model.memo,
            mileage = model.mileage,
            allowOrder = model.allowOrder
        )
    }