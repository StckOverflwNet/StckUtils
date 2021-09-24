package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

object NoBlockBreak : Challenge() {

    override val id: String = "no-block-break"
    override val name: String = "§eNo Block Break"
    override val material: Material = Material.GOLDEN_PICKAXE
    override val description: List<String> = listOf(
        " ",
        "§7When you break a block the challenge is over.",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!event.player.isPlaying()) {
            return
        }
        lose("${event.player.name} broke a Block.")
    }
}
