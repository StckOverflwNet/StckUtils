package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerToggleSneakEvent
import java.util.*

object NoSneak : Challenge() {

    override val id: String = "no-sneak"
    override val name: String = "§eNo Sneak"
    override val material: Material = Material.CHAINMAIL_BOOTS
    override val description: List<String> = listOf(
        " ",
        "§7When you sneak the challenge is over.",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onSneak(event: PlayerToggleSneakEvent) {
        if (!event.player.isPlaying()) {
            return
        }
        if (event.isSneaking) {
            lose("${event.player.name} sneaked.")
        }
    }
}
