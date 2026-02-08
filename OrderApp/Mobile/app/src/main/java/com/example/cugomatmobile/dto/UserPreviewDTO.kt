package com.example.cugomatmobile.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UserPreviewDTO @JsonCreator constructor(
    @JsonProperty("id")
    val id: Int?,
    @JsonProperty("username")
    val username: String?,
    @JsonProperty("firstName")
    val firstName: String?,
    @JsonProperty("lastName")
    val lastName: String?,
    @JsonProperty("email")
    val email: String?,
    @JsonProperty("phone")
    val phone: String?
)