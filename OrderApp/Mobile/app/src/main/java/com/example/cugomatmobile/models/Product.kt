package com.example.cugomatmobile.models

import java.math.BigDecimal
data class Product(
    val id: Int?,
    val name: String?,
    val price: BigDecimal?,
    val category: Category?,
    var quantity: Int?
) {
    val totalPrice: BigDecimal
        get() = (price ?: BigDecimal.ZERO)
            .multiply(BigDecimal(quantity ?: 0))
}
