package com.example.cugomatmobile.models

import java.math.BigDecimal

object CartManager {
    private val productList = mutableListOf<Product>()

    fun addProduct(product: Product, quantity: Int) {
        val existingItem = productList.find { it.id == product.id }
        if (existingItem != null) {
            existingItem.quantity = (existingItem.quantity ?: 0) + quantity
            if (existingItem.quantity!! <= 0) removeProduct(product.id!!)
        } else if (quantity > 0) {
            product.quantity = quantity
            productList.add(product)
        }
    }
    fun getItems(): List<Product> = productList.toList()
    fun getTotalPrice(): BigDecimal =
        productList.fold(BigDecimal.ZERO) { acc, product -> acc + product.totalPrice }
    fun removeProduct(productId: Int) {
        productList.removeAll { it.id == productId }
    }
    fun clear() {
        productList.clear()
    }

    fun getCheapestProduct(): Product? =
        productList.minByOrNull { it.price ?: BigDecimal.ZERO }
}
