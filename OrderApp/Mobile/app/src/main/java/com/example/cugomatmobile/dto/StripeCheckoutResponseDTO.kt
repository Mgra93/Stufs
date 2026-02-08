package com.example.cugomatmobile.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class StripeCheckoutResponseDTO @JsonCreator constructor(
    @JsonProperty("checkoutUrl")
    val checkoutUrl: String,
    @JsonProperty("sessionId")
    val sessionId: String
)
