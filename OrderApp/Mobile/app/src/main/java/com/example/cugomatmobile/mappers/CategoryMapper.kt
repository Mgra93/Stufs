package com.example.cugomatmobile.mappers

import com.example.cugomatmobile.dto.CategoryDTO
import com.example.cugomatmobile.models.Category

object CategoryMapper {
    fun toEntity(dto: CategoryDTO): Category {
        return Category(
            id = dto.id,
            name = dto.name
        )
    }

    fun toEntityList(dtoList: List<CategoryDTO>): List<Category> {
        return dtoList.map { toEntity(it) }
    }
}