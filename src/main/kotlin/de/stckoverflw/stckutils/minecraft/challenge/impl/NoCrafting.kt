package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import java.util.*

object NoCrafting : Challenge() {

    override val id: String = "no-crafting"
    override val material: Material = Material.CRAFTING_TABLE
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        if (!(event.whoClicked as Player).isPlaying()) {
            return
        }
        lose(id, arrayOf(event.whoClicked.name, event.recipe.result.type.name))
    }
}
