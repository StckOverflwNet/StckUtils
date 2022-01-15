package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.extensions.bukkit.kill
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import java.util.Locale

object Medusa : Challenge() {

    override val id: String = "medusa"
    override val material: Material = Material.STONE
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (event.player.isPlaying()) {
            val eyeLocation = event.player.eyeLocation
            event.player.getNearbyEntities(48.0, 48.0, 48.0).forEach { entity ->
                if (entity is Mob && !entity.isDead) {
                    val toEntity = entity.eyeLocation.toVector().subtract(eyeLocation.toVector())
                    val dot: Double = toEntity.normalize().dot(eyeLocation.direction)

                    if (dot > 0.99) {
                        entity.killer = event.player
                        entity.kill()
                        entity.location.block.type = Material.STONE
                        entity.eyeLocation.block.type = Material.STONE
                    }
                }
            }
        }
    }
}
