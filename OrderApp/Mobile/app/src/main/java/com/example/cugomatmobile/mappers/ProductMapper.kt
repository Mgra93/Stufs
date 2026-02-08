package com.example.cugomatmobile.mappers

import com.example.cugomatmobile.dto.ProductDTO
import com.example.cugomatmobile.models.Product

object ProductMapper {
    fun toEntity(dto: ProductDTO): Product {
        return Product(
            id = dto.id,
            name = dto.name,
            price = dto.price,
            category = dto.category?.let { CategoryMapper.toEntity(it) },
            quantity =  dto.quantity
        )
    }

    fun toEntityList(dtoList: List<ProductDTO>): List<Product> {
        return dtoList.map { toEntity(it) }
    }
}