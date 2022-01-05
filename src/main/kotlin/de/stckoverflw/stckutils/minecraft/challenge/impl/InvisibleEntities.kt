package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.CreatureSpawnEvent
import java.util.*

object InvisibleEntities : Challenge() {

    override val id: String = "invisible-entities"
    override val name: String = "§fInvisible Entities"
    override val material: Material = Material.WHITE_BANNER
    override val description: List<String> = listOf(
        " ",
        "§7Every Mob is invisible.",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        if (event.entity.type == EntityType.PLAYER) {
            return
        }
        event.entity.isInvisible = true
    }
}
