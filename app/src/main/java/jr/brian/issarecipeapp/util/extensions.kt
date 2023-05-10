package jr.brian.issarecipeapp.util

fun String.ifBlankUse(value: String): String {
    if (isBlank()) {
        return value
    }
    return this
}