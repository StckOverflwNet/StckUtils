package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent

object NoCrafting : Challenge() {
    override val id: String = "no-crafting"
    override val name: String = "§aNo Crafting"
    override val material: Material = Material.CRAFTING_TABLE
    override val description: List<String> = listOf(
        " ",
        "§7When you craft something the challenge is over.",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        lose("${event.whoClicked.name} crafted ${event.recipe.result.type.name}.")
    }
}
