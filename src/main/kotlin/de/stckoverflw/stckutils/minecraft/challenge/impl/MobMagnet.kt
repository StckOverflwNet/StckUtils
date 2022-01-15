package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.Locale

object MobMagnet : Challenge() {

    override val id: String = "mob-magnet"
    override val material: Material = Material.RAW_IRON_BLOCK
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onDeath(event: EntityDamageByEntityEvent) {
        if (event.entity !is Mob && event.damager !is Player) {
            return
        }
        val mob = event.entity as Mob
        val player = event.damager as Player

        // ignore players that are currently not playing
        if (!player.isPlaying()) {
            return
        }

        // ignore mobs that do not die after the damage is applied
        if (mob.health - event.damage > 0) {
            return
        }

        event.entity.getNearbyEntities(48.0, 48.0, 48.0).forEach { entity ->
            if (entity.type == event.entity.type) {
                entity.teleport(event.entity.location)
            }
        }
    }
}
