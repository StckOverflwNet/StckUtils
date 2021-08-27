package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import io.papermc.paper.event.player.PlayerTradeEvent
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler

object NoVillagerTrade : Challenge() {
    override val id: String = "no-villager-trade"
    override val name: String = "${KColors.BROWN}No Villager Trade"
    override val material: Material = Material.VILLAGER_SPAWN_EGG
    override val description: List<String> = listOf(
        " ",
        "§7When you trade with a villager",
        "§7or wandering trader the challenge is over.",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onVillagerTrade(event: PlayerTradeEvent) {
        lose("${event.player.name} traded with a ${event.villager.name}.")
    }
}
