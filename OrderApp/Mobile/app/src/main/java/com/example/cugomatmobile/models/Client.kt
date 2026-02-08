package com.example.cugomatmobile.models

import java.time.LocalDateTime

data class Client(
    val id: Int?,
    val name: String?,
    val code: String?,
    val address: String?,
    val phone: String?,
    val oib: String?,
    val locationSecret: String?,
    val webPageUrl: String?,
    val licenseExpiryTime: LocalDateTime?
)
