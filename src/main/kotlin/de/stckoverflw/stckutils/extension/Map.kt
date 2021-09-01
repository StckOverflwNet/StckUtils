package de.stckoverflw.stckutils.extension

fun HashMap<*, *>.getKey(target: Any): Any? {
    for ((key, value) in this) {
        if (target == value) {
            return key
        }
    }
    return null
}
