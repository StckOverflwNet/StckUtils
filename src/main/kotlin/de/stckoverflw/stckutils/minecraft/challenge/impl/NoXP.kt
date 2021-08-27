package de.stckoverflw.stckutils.minecraft.challenge.impl

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler

object NoXP : Challenge() {
    override val id: String = "no-xp"
    override val name: String = "§eNo XP"
    override val material: Material = Material.EXPERIENCE_BOTTLE
    override val description: List<String> = listOf(
        " ",
        "§7When you pick up xp the challenge is over.",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onXP(event: PlayerPickupExperienceEvent) {
        lose("${event.player.name} picked up XP.")
    }
}
