package com.example.cugomatmobile.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RefreshTokenRequestDTO @JsonCreator constructor (
    @JsonProperty("token")
    val token: String?,
)