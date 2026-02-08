package com.example.cugomatmobile.models

object DataManager {
    var user: String? = null
    var clientCode: String? = null
    var tableCode: String? = null

    fun clear() {
        user = null
        clientCode = null
        tableCode = null
    }
}