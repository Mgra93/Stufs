package com.example.cugomatmobile.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import java.time.LocalDateTime

data class ClientDTO @JsonCreator constructor (
    @JsonProperty("id")
    val id: Int?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("code")
    val code: String?,
    @JsonProperty("address")
    val address: String?,
    @JsonProperty("phone")
    val phone: String?,
    @JsonProperty("oib")
    val oib: String?,
    @JsonProperty("active")
    val active: Boolean?,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SS")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonProperty("licenseExpiryTime")
    val licenseExpiryTime: LocalDateTime?,
    @JsonProperty("locationSecret")
    val locationSecret: String?,
    @JsonProperty("webPageUrl")
    val webPageUrl: String?,
    @JsonProperty("vipDayActive")
    val vipDayActive: Boolean?,
    @JsonProperty("vipDayCode")
    val vipDayCode: Int?
)