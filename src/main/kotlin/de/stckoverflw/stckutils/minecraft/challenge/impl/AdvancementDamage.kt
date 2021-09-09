package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAdvancementDoneEvent

object AdvancementDamage : Challenge() {

    private var damage: Double
        get() = Config.gameChangeConfig.getSetting(id, "damage") as Double? ?: 2.0
        set(value) = Config.gameChangeConfig.setSetting(id, "damage", value)

    override val id: String = "advancement-damage"
    override val name: String = "§bAdvancement Damage"
    override val material: Material = Material.DAMAGED_ANVIL
    override val description: List<String> = listOf(
        " ",
        "§7For every advancement you get, you lose hp.",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onAchievement(event: PlayerAdvancementDoneEvent) {
        if (!event.player.isPlaying()) return
        event.player.damage(damage)
    }
}
