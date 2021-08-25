package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent

object NoDeath : Challenge() {
    override val id: String = "no-death"
    override val name: String = "§4No Death"
    override val material: Material = Material.SKELETON_SKULL
    override val description: List<String> = listOf(
        " ",
        "§7When you die the challenge is over.",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onCraft(event: PlayerDeathEvent) {
        lose("${event.entity.name} died.")
    }
}
