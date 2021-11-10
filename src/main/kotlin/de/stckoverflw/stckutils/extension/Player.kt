package de.stckoverflw.stckutils.extension

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.util.*
import net.axay.kspigot.extensions.geometry.LocationArea
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.main.KSpigotMainInstance
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.resetWorlds() {
    Config.resetSettingsConfig.shouldReset = true
    onlinePlayers.forEach {
        it.kick(
            Component.join(
                Component.newline(),
                Component.text("§7The Worlds are §cresetting"),
                Component.text("§7Reset started by §9$name"),
            )
        )
    }
    Bukkit.getServer().spigot().restart()
}

fun Player.setSavedInventory() {
    if (persistentDataContainer.has(Namespaces.CHALLENGE_INVENTORY_CONTENTS)) {
        inventory.clear()
        inventory.contents =
            persistentDataContainer.get(Namespaces.CHALLENGE_INVENTORY_CONTENTS)
            ?.let { fromBase64(it) } as Array<out ItemStack?>
        persistentDataContainer.remove(Namespaces.CHALLENGE_INVENTORY_CONTENTS)
    }
}

fun Player.saveInventory() {
    persistentDataContainer.set(
        Namespaces.CHALLENGE_INVENTORY_CONTENTS,
        toBase64(inventory.contents as Array<ItemStack>)
    )
}

fun Player.isHidden() = this.persistentDataContainer.get(Namespaces.CHALLENGE_FUNCTION_HIDDEN) == 1.toByte()

fun Player.hide() {
    this.persistentDataContainer.set(Namespaces.CHALLENGE_FUNCTION_HIDDEN, 1.toByte())

    onlinePlayers.forEach { player ->
        if (player == this) return@forEach
        if (player.persistentDataContainer.get(Namespaces.CHALLENGE_FUNCTION_HIDDEN) == 1.toByte()) {
            player.showPlayer(KSpigotMainInstance, this)
        } else {
            player.hidePlayer(KSpigotMainInstance, this)
        }
        this.showPlayer(KSpigotMainInstance, player)
    }
}

fun Player.reveal() {
    this.persistentDataContainer.set(Namespaces.CHALLENGE_FUNCTION_HIDDEN, 0.toByte())

    onlinePlayers.forEach { player ->
        if (player == this) return@forEach
        if (player.persistentDataContainer.get(Namespaces.CHALLENGE_FUNCTION_HIDDEN) == 1.toByte()) {
            this.hidePlayer(KSpigotMainInstance, player)
        } else {
            this.showPlayer(KSpigotMainInstance, player)
        }
        player.showPlayer(KSpigotMainInstance, this)
    }
}

fun Player.isPlaying() = !this.isHidden() && this.gameMode == GameMode.SURVIVAL

fun Player.isInArea(location: Location, radius: Double): Boolean {
    val add = Location(location.world, location.x - radius, 0.0, location.z + radius)
    val sub = Location(location.world, location.x + radius, 0.0, location.z - radius)

    return LocationArea(add.toBlockLocation(), sub.toBlockLocation()).isInArea(this.location, false, 0)
}

fun Player.isInArea(location: Location, location2: Location): Boolean =
    LocationArea(location, location2).isInArea(this.location, false, 0)
