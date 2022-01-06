package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import io.papermc.paper.event.player.PlayerTradeEvent
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import java.util.*

object NoVillagerTrade : Challenge() {

    override val id: String = "no-villager-trade"
    override val material: Material = Material.VILLAGER_SPAWN_EGG
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onVillagerTrade(event: PlayerTradeEvent) {
        if (!event.player.isPlaying()) {
            return
        }
        lose(id, arrayOf(event.player.name, event.villager.name))
    }
}
