package com.example.cugomatmobile.mappers

import com.example.cugomatmobile.dto.OrderDTO
import com.example.cugomatmobile.enums.OrderStatus
import com.example.cugomatmobile.models.Order

object OrderMapper {
    fun toEntity(dto: OrderDTO?): Order? {
        if (dto == null) return null

        return Order(
            id = dto.id,
            createdOn = dto.createdOn,
            totalPrice = dto.totalPrice,
            hasDiscount = dto.hasDiscount,
            finalPrice = dto.finalPrice,
            status = dto.status?.let { OrderStatus.fromCode(it) },
            tableCode = dto.tableCode,
            client = dto.client?.let { ClientMapper.toEntity(it) },
            user = dto.user?.let { UserMapper.toPreviewEntity(it) },
            worker = dto.worker?.let { UserMapper.toPreviewEntity(it) },
            products = dto.products?.let { ProductMapper.toEntityList(it) }
        )
    }

    fun toEntityList(dtoList: List<OrderDTO>): List<Order?> {
        return dtoList.map { toEntity(it) }
    }
}