package com.example.cugomatmobile.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class ProductDTO @JsonCreator constructor(
    @JsonProperty("id")
    val id: Int?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("price")
    val price: BigDecimal?,
    @JsonProperty("category")
    val category: CategoryDTO?,
    @JsonProperty("sumPrice")
    val sumPrice: BigDecimal?,
    @JsonProperty("quantity")
    val quantity: Int?
)