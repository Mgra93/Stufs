package com.example.cugomatmobile.models

data class StripeCheckoutResponse(
    val checkoutUrl: String?,
    val sessionId: String?
)