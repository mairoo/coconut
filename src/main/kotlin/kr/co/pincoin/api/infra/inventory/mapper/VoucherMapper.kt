package kr.co.pincoin.api.infra.inventory.mapper

import kr.co.pincoin.api.domain.inventory.model.Voucher
import kr.co.pincoin.api.infra.inventory.entity.VoucherEntity

fun VoucherEntity?.toModel(): Voucher? =
    this?.let { entity ->
        Voucher.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            code = entity.code,
            remarks = entity.remarks,
            status = entity.status,
            productId = entity.productId
        )
    }

fun List<VoucherEntity>?.toModelList(): List<Voucher> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun Voucher?.toEntity(): VoucherEntity? =
    this?.let { model ->
        VoucherEntity.of(
            id = model.id,
            isRemoved = model.isRemoved,
            code = model.code,
            remarks = model.remarks,
            status = model.status,
            productId = model.productId
            // created, modified: 매핑 안 함 JPA Auditing 관리 필드
        )
    }

fun List<Voucher>?.toEntityList(): List<VoucherEntity> =
    this?.mapNotNull { it.toEntity() } ?: emptyList()
