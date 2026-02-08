package com.example.cugomatmobile.mappers

import com.example.cugomatmobile.dto.UserDTO
import com.example.cugomatmobile.dto.UserPreviewDTO
import com.example.cugomatmobile.models.User
import com.example.cugomatmobile.models.UserPreview

object UserMapper {
    fun toEntity(dto: UserDTO): User {
        return User(
            id = dto.id,
            username = dto.username,
            password = dto.password,
            firstName = dto.firstName,
            lastName = dto.lastName,
            email = dto.email,
            phone = dto.phone
        )
    }

    fun toPreviewEntity(dto: UserPreviewDTO): UserPreview {
        return UserPreview(
            id = dto.id,
            username = dto.username,
            firstName = dto.firstName,
            lastName = dto.lastName,
            email = dto.email,
            phone = dto.phone
        )
    }

    fun toEntityList(dtoList: List<UserDTO>): List<User> {
        return dtoList.map { toEntity(it) }
    }
}