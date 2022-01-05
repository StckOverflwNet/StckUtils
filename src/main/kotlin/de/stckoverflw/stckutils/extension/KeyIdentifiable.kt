package de.stckoverflw.stckutils.extension

interface KeyIdentifiable {
    val key: String
}

inline fun <reified T> fromKey(key: String): T? where T : Enum<T>, T : KeyIdentifiable {
    return enumValues<T>().find { it.key == key }
}
