package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.extensions.bukkit.kill
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageEvent

object NoFallDamage : Challenge() {
    override val id: String = "no-fall-damage"
    override val name: String = "§aNo Fall Damage"
    override val material: Material = Material.LEATHER_BOOTS
    override val description: List<String> = listOf(
        " ",
        "§7When you get fall damgage the challenge is over."
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onFallDamage(event: EntityDamageByBlockEvent) {
        if (event.cause == EntityDamageEvent.DamageCause.FALL && event.entity is Player)
            lose("${event.entity.name} took fall damage.")
    }
}
