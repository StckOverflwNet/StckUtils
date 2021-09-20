package de.stckoverflw.stckutils.listener

import de.stckoverflw.stckutils.minecraft.timer.Timer
import net.axay.kspigot.main.KSpigotMainInstance
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.persistence.PersistentDataType

class RespawnListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        player.teleport(player.location)
        if (Timer.running) {
            player.persistentDataContainer.set(
                NamespacedKey(KSpigotMainInstance, "death_location_world"),
                PersistentDataType.STRING,
                player.location.world.name
            )
            player.persistentDataContainer.set(
                NamespacedKey(KSpigotMainInstance, "death_location_x"),
                PersistentDataType.DOUBLE,
                player.location.x
            )
            player.persistentDataContainer.set(
                NamespacedKey(KSpigotMainInstance, "death_location_y"),
                PersistentDataType.DOUBLE,
                player.location.y
            )
            player.persistentDataContainer.set(
                NamespacedKey(KSpigotMainInstance, "death_location_z"),
                PersistentDataType.DOUBLE,
                player.location.z
            )
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onRespawn(event: PlayerRespawnEvent) {
        if (!Timer.running) {
            val player = event.player
            if (
                player.persistentDataContainer.has(
                    NamespacedKey(KSpigotMainInstance, "death_location_world"), PersistentDataType.STRING
                ) &&
                player.persistentDataContainer.has(
                    NamespacedKey(KSpigotMainInstance, "death_location_x"), PersistentDataType.DOUBLE
                ) &&
                player.persistentDataContainer.has(
                    NamespacedKey(KSpigotMainInstance, "death_location_y"), PersistentDataType.DOUBLE
                ) &&
                player.persistentDataContainer.has(
                    NamespacedKey(KSpigotMainInstance, "death_location_z"), PersistentDataType.DOUBLE
                )
            ) {
                val location =
                    Location(
                        Bukkit.getWorld(
                            player.persistentDataContainer.get(
                                NamespacedKey(KSpigotMainInstance, "death_location_world"), PersistentDataType.STRING
                            )!!
                        )!!,
                        player.persistentDataContainer.get(
                            NamespacedKey(KSpigotMainInstance, "death_location_x"), PersistentDataType.DOUBLE
                        )!!,
                        player.persistentDataContainer.get(
                            NamespacedKey(KSpigotMainInstance, "death_location_y"), PersistentDataType.DOUBLE
                        )!!,
                        player.persistentDataContainer.get(
                            NamespacedKey(KSpigotMainInstance, "death_location_z"), PersistentDataType.DOUBLE
                        )!!
                    )
                taskRunLater(2, true) {
                    player.gameMode = GameMode.SPECTATOR
                    player.teleportAsync(location)
                }
            }
        }
    }
}
