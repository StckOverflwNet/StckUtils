package de.stckoverflw.stckutils.extension

fun HashMap<*, *>.getKey(value: Any): Any? {
    for ((k, v) in this) {
        if (value == v) {
            return k
        }
    }
    return null
}
