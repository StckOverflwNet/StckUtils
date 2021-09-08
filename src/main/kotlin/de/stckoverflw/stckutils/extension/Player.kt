package de.stckoverflw.stckutils.extension

import de.stckoverflw.stckutils.config.Config
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.main.KSpigotMainInstance
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun Player.resetWorlds() {
    Config.resetSettings.shouldReset = true
    onlinePlayers.forEach {
        it.kick(
            Component.join(
                Component.newline(),
                Component.text("§7The Worlds are §cresetting"),
                Component.text("§7Reset started by §9${name}"),
            )
        )
    }
    Bukkit.getServer().spigot().restart()
}

fun Player.setSavedInventory() {
    if (persistentDataContainer.has(NamespacedKey(KSpigotMainInstance, "challenge-inventory-contents"), PersistentDataType.STRING)) {
        inventory.clear()
        inventory.contents =
            persistentDataContainer.get(NamespacedKey(KSpigotMainInstance, "challenge-inventory-contents"), PersistentDataType.STRING)
                ?.let { it1 -> fromBase64(it1) } as Array<out ItemStack?>
        persistentDataContainer.remove(NamespacedKey(KSpigotMainInstance, "challenge-inventory-contents"))
    }
}

fun Player.saveInventory() {
    persistentDataContainer.set(
        NamespacedKey(KSpigotMainInstance, "challenge-inventory-contents"),
        PersistentDataType.STRING,
        toBase64(inventory.contents as Array<ItemStack>)
    )
}

fun Player.isHidden() =
    this.persistentDataContainer.get(NamespacedKey(KSpigotMainInstance, "challenge-funciton-hidden"), PersistentDataType.BYTE) == 1.toByte()

fun Player.hide() {
    this.persistentDataContainer.set(NamespacedKey(KSpigotMainInstance, "challenge-funciton-hidden"), PersistentDataType.BYTE, 1.toByte())
    println(onlinePlayers.joinToString { player -> player.name })
    onlinePlayers.forEach { player ->
        if (player == this) return@forEach
        if (player.persistentDataContainer.get(
                NamespacedKey(KSpigotMainInstance, "challenge-funciton-hidden"),
                PersistentDataType.BYTE
            ) == 1.toByte()
        ) {
            this.showPlayer(KSpigotMainInstance, player)
            player.showPlayer(KSpigotMainInstance, this)
        } else {
            player.hidePlayer(KSpigotMainInstance, this)
            this.showPlayer(KSpigotMainInstance, player)
        }
    }
}

fun Player.reveal() {
    this.persistentDataContainer.set(NamespacedKey(KSpigotMainInstance, "challenge-funciton-hidden"), PersistentDataType.BYTE, 0.toByte())
    println(onlinePlayers.joinToString { player -> player.name })
    onlinePlayers.forEach { player ->
        if (player == this) return@forEach
        if (player.persistentDataContainer.get(
                NamespacedKey(KSpigotMainInstance, "challenge-funciton-hidden"),
                PersistentDataType.BYTE
            ) == 1.toByte()
        ) {
            this.hidePlayer(KSpigotMainInstance, player)
            player.showPlayer(KSpigotMainInstance, this)
        } else {
            player.showPlayer(KSpigotMainInstance, this)
            this.showPlayer(KSpigotMainInstance, player)
        }
    }
}

fun Player.isPlaying() = !this.isHidden() && this.gameMode == GameMode.SURVIVAL
