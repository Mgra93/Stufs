package com.example.cugomatmobile.mappers

import com.example.cugomatmobile.dto.StripeCheckoutResponseDTO
import com.example.cugomatmobile.models.StripeCheckoutResponse

object StripeMapper {
    fun toEntity(dto: StripeCheckoutResponseDTO): StripeCheckoutResponse {
        return StripeCheckoutResponse(
            checkoutUrl = dto.checkoutUrl,
            sessionId = dto.sessionId
        )
    }
}