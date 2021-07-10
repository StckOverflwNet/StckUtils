package de.stckoverflw.stckutils.extension

import com.google.gson.JsonElement

operator fun JsonElement.get(name: String): JsonElement = asJsonObject[name]
operator fun JsonElement.get(index: Int): JsonElement = asJsonArray[index]