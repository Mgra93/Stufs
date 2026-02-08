package com.example.cugomatmobile.dto

import java.math.BigDecimal

data class OrderCreateDTO(
    val user: String?,
    val clientCode: String?,
    val tableCode: String?,
    val productList : List<ProductDTO>?,
    val totalPrice : BigDecimal?,
    val hasDiscount : Boolean?,
    val finalPrice : BigDecimal?
)