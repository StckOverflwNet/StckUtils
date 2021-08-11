package de.stckoverflw.stckutils.extension

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

fun toBase64(itemstacks: Array<ItemStack>): String {
    return try {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)

        // Write the size of the inventory
        dataOutput.writeInt(itemstacks.size)

        // Save every element in the list
        itemstacks.forEach {
            dataOutput.writeObject(it)
        }

        // Serialize that array
        dataOutput.close()
        Base64Coder.encodeLines(outputStream.toByteArray())
    } catch (e: Exception) {
        throw IllegalStateException("Unable to save ItemStackArray.", e)
    }
}

fun fromBase64(base64String: String): Array<ItemStack?> {
    return try {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(base64String))
        val dataInput = BukkitObjectInputStream(inputStream)
        val items = arrayOfNulls<ItemStack>(dataInput.readInt())

        // Read the serialized inventory
        for (i in items.indices) {
            val read = dataInput.readObject() ?: continue
            items[i] = read as ItemStack
        }

        dataInput.close()
        items
    } catch (e: ClassNotFoundException) {
        throw IOException("Unable to decode class type.", e)
    }
}