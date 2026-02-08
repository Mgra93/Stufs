package com.example.cugomatmobile.mappers

import com.example.cugomatmobile.dto.JwtResponseDTO
import com.example.cugomatmobile.models.JwtResponse

object JwtResponseMapper {
    fun toEntity(responseDTO: JwtResponseDTO): JwtResponse {
        return JwtResponse(
            accessToken = responseDTO.accessToken,
            refreshToken = responseDTO.refreshToken,
            errorMessage = responseDTO.errorMessage
        )
    }
}