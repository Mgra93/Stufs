package com.example.cugomatmobile.mappers

import com.example.cugomatmobile.dto.ClientDTO
import com.example.cugomatmobile.models.Client

object ClientMapper {
    fun toEntity(dto: ClientDTO): Client {
        return Client(
            id = dto.id,
            name = dto.name,
            code = dto.code,
            address = dto.address,
            phone = dto.phone,
            oib = dto.oib,
            locationSecret = dto.locationSecret,
            webPageUrl = dto.webPageUrl,
            licenseExpiryTime = dto.licenseExpiryTime
        )
    }
}