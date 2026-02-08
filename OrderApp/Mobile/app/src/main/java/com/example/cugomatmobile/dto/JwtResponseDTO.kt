package com.example.cugomatmobile.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class JwtResponseDTO @JsonCreator constructor(
    @JsonProperty("accessToken")
    val accessToken: String?,
    @JsonProperty("refreshToken")
    val refreshToken: String?,
    @JsonProperty("errorMessage")
    val errorMessage: String?
)
