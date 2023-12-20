package jr.brian.issarecipeapp.util

import android.content.Context
import android.widget.Toast
import java.net.URL

fun String.ifBlankUse(value: String): String {
    if (isBlank()) {
        return value
    }
    return this
}

fun String.isUrl(): Boolean {
    return try {
        URL(this)
        true
    } catch (e: Exception) {
        false
    }
}

fun Context.showToast(text: String, isLongToast: Boolean = false) {
    val duration = if (isLongToast) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    Toast.makeText(this, text, duration).show()
}