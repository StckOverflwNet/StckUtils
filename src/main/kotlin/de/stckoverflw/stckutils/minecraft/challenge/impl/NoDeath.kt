package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.Locale

object NoDeath : Challenge() {

    override val id: String = "no-death"
    override val material: Material = Material.SKELETON_SKULL
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        if (!event.entity.isPlaying()) {
            return
        }
        event.entity.inventory.clear()
        lose(listOf(event.entity.name()))
    }
}
