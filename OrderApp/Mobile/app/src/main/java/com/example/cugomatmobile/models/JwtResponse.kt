package com.example.cugomatmobile.models

data class JwtResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val errorMessage: String?
)
