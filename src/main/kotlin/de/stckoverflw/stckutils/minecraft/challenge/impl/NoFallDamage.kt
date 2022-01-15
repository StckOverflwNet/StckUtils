package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import java.util.Locale

object NoFallDamage : Challenge() {

    override val id: String = "no-fall-damage"
    override val material: Material = Material.LEATHER_BOOTS
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onFallDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) {
            return
        }

        val player = event.entity as Player
        if (!player.isPlaying()) {
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.FALL) {
            lose(listOf(player.name()))
        }
    }
}
