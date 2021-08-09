package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.extensions.bukkit.kill
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent

object NoBlockPlace : Challenge() {
    override val id: String = "no-block-place"
    override val name: String = "§eNo Block Place"
    override val material: Material = Material.GOLDEN_PICKAXE
    override val description: List<String> = listOf(
        " ",
        "§7When you place a block the challenge is over."
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        lose("${event.player.name} placed a Block.")
    }
}
