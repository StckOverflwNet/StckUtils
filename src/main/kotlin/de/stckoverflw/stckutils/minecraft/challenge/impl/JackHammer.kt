package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import java.util.Locale

object JackHammer : Challenge() {

    override val id: String = "jackhammer"
    override val material: Material = Material.IRON_PICKAXE
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!event.player.isPlaying()) {
            return
        }

        for (i in (event.block.y - 1) downTo 0) {
            val block = event.block.world.getBlockAt(event.block.x, i, event.block.z)
            if (block.type.blastResistance > 1200.0F) return
            block.type = Material.AIR
        }
    }
}
