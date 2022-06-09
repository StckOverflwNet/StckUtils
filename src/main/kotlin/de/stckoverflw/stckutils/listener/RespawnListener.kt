package de.stckoverflw.stckutils.listener

import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.Namespaces
import de.stckoverflw.stckutils.util.get
import de.stckoverflw.stckutils.util.set
import net.axay.kspigot.extensions.server
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent

class RespawnListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        player.teleport(player.location)
        if (Timer.running) {
            player.persistentDataContainer.set(Namespaces.DEATH_LOCATION_WORLD, player.location.world.name)
            player.persistentDataContainer.set(Namespaces.DEATH_LOCATION_X, player.location.x)
            player.persistentDataContainer.set(Namespaces.DEATH_LOCATION_Y, player.location.y)
            player.persistentDataContainer.set(Namespaces.DEATH_LOCATION_Z, player.location.z)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onRespawn(event: PlayerRespawnEvent) {
        if (!Timer.running) {
            val player = event.player

            val deathWorld = player.persistentDataContainer.get(Namespaces.DEATH_LOCATION_WORLD) ?: return
            val deathLocationX = player.persistentDataContainer.get(Namespaces.DEATH_LOCATION_X) ?: return
            val deathLocationY = player.persistentDataContainer.get(Namespaces.DEATH_LOCATION_Y) ?: return
            val deathLocationZ = player.persistentDataContainer.get(Namespaces.DEATH_LOCATION_Z) ?: return

            val location = Location(server.getWorld(deathWorld)!!, deathLocationX, deathLocationY, deathLocationZ)

            player.gameMode = GameMode.SPECTATOR
            player.teleportAsync(location)
        }
    }
}
