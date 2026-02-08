package com.example.cugomatmobile.enums

import androidx.annotation.StringRes
import com.example.cugomatmobile.R

enum class OrderStatus(val code: Int, @StringRes val transl: Int) {
    CREATED(0, R.string.order_status_created),
    RECEIVED(1, R.string.order_status_received),
    COMPLETED(2, R.string.order_status_completed),
    REJECTED(3, R.string.order_status_rejected);

    companion object {
        fun fromCode(code: Int): OrderStatus? {
            return values().find { it.code == code }
        }
    }
}
