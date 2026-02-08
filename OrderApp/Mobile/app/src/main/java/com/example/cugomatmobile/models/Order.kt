package com.example.cugomatmobile.models

import com.example.cugomatmobile.enums.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class Order(
    val id: Int?,
    val createdOn: LocalDateTime?,
    val totalPrice: BigDecimal?,
    val hasDiscount: Boolean?,
    val finalPrice: BigDecimal?,
    val status: OrderStatus?,
    val tableCode: String?,
    val client: Client?,
    val user: UserPreview?,
    val worker: UserPreview?,
    val products: List<Product>?,
)
