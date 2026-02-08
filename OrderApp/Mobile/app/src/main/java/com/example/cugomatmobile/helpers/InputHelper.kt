package com.example.cugomatmobile.helpers

import android.util.Patterns

object InputHelper {
    private val phoneRegex = Regex("""^[\d\s\-\/]+$""")

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        if (phone.isBlank()) return true
        return phoneRegex.matches(phone)
    }
}