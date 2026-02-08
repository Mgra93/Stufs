package com.example.cugomatmobile.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UserDTO @JsonCreator constructor (
    @JsonProperty("id")
    val id: Int = 0,
    @JsonProperty("username")
    val username: String = "",
    @JsonProperty("password")
    val password: String =  "",
    @JsonProperty("firstName")
    val firstName: String =  "",
    @JsonProperty("lastName")
    val lastName: String = "",
    @JsonProperty("email")
    val email: String,
    @JsonProperty("phone")
    val phone: String = ""
)