package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerToggleSneakEvent

object NoSneak : Challenge() {
    override val id: String = "no-sneak"
    override val name: String = "§eNo Sneak"
    override val material: Material = Material.CHAINMAIL_BOOTS
    override val description: List<String> = listOf(
        " ",
        "§7When you sneak the challenge is over.",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onSneak(event: PlayerToggleSneakEvent) {
        if (event.isSneaking)
            lose("${event.player.name} sneaked.")
    }
}
