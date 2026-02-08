package com.example.cugomatmobile.models

data class User (
    val id: Int?,
    val username: String?,
    val password: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phone: String?
) {
    val fullName: String
        get() = "$firstName $lastName"
}