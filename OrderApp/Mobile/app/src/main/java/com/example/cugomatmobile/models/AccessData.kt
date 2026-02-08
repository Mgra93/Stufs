package com.example.cugomatmobile.models

data class AccessData(
    var accessToken: String?,
    var refreshToken: String?
){
    val bearerToken:String? get() = if(accessToken != null) "Bearer $accessToken" else null;
}