package de.stckoverflw.stckutils.util

import net.axay.kspigot.main.KSpigotMainInstance
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class TypedNamespace<T, Z>(val namespace: NamespacedKey, val type: PersistentDataType<T, Z>) {

    constructor(key: String, type: PersistentDataType<T, Z>) : this(NamespacedKey(KSpigotMainInstance, key), type)
}

fun <T, Z> PersistentDataContainer.get(typedNamespace: TypedNamespace<T, Z>): Z? {
    return this.get(typedNamespace.namespace, typedNamespace.type)
}

fun <T, Z> PersistentDataContainer.has(typedNamespace: TypedNamespace<T, Z>): Boolean {
    return this.has(typedNamespace.namespace, typedNamespace.type)
}

fun <T, Z> PersistentDataContainer.set(typedNamespace: TypedNamespace<T, Z>, value: Z) {
    this.set(typedNamespace.namespace, typedNamespace.type, value!!)
}

fun <T, Z> PersistentDataContainer.remove(typedNamespace: TypedNamespace<T, Z>) {
    this.remove(typedNamespace.namespace)
}

object Namespaces {

    val COLOR_COMPOUND_VALUE = TypedNamespace("color-compound-value", PersistentDataType.INTEGER)
    val CHALLENGE_INVENTORY_CONTENTS = TypedNamespace("challenge-inventory-contents", PersistentDataType.STRING)
    val DEATH_LOCATION_WORLD = TypedNamespace("death_location_world", PersistentDataType.STRING)
    val DEATH_LOCATION_X = TypedNamespace("death_location_x", PersistentDataType.DOUBLE)
    val DEATH_LOCATION_Y = TypedNamespace("death_location_y", PersistentDataType.DOUBLE)
    val DEATH_LOCATION_Z = TypedNamespace("death_location_z", PersistentDataType.DOUBLE)
}
