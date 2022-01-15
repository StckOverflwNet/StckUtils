package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent
import java.util.Locale

object NoBlockPlace : Challenge() {

    override val id: String = "no-block-place"
    override val material: Material = Material.GRASS_BLOCK
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!event.player.isPlaying()) {
            return
        }
        lose(listOf(event.player.name()))
    }
}
