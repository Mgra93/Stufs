package com.example.cugomatmobile.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.math.BigDecimal
import java.time.LocalDateTime

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer

data class OrderDTO @JsonCreator constructor(
    @JsonProperty("id")
    val id: Int?,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonProperty("name")
    val createdOn: LocalDateTime?,
    @JsonProperty("price")
    val totalPrice: BigDecimal?,
    @JsonProperty("hasDiscount")
    val hasDiscount: Boolean?,
    @JsonProperty("finalPrice")
    val finalPrice: BigDecimal?,
    @JsonProperty("category")
    val status: Int?,
    @JsonProperty("tableCode")
    val tableCode: String?,
    @JsonProperty("client")
    val client: ClientDTO?,
    @JsonProperty("user")
    val user: UserPreviewDTO?,
    @JsonProperty("worker")
    val worker: UserPreviewDTO?,
    @JsonProperty("products")
    val products: List<ProductDTO>?,
)