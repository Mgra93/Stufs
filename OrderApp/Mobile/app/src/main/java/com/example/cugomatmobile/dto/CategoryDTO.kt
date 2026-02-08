package com.example.cugomatmobile.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class CategoryDTO @JsonCreator constructor(
    @JsonProperty("id")
    val id: Int?,
    @JsonProperty("name")
    val name: String?
)
