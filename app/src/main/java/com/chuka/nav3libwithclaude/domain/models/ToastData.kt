package com.chuka.nav3libwithclaude.domain.models

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize

@Parcelize
data class ToastData(
    val message: String,
    val duration: Int,
    val backgroundColor: Long,
) : Parcelable {
    companion object {
        const val LENGTH_SHORT = 0
        const val LENGTH_LONG = 1

        fun create(
            message: String,
            duration: Int = LENGTH_SHORT,
            backgroundColor: Long = 0xff000000,
        ): ToastData {
            return ToastData(message, duration, backgroundColor)
        }
    }
}
