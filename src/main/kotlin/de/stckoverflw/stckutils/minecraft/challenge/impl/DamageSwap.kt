package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent

object DamageSwap : Challenge() {

    override val id: String = "damage-swap"
    override val name: String = "§4DamageSwap"
    override val material: Material = Material.SHIELD
    override val description: List<String> = listOf(
        " ",
        "§7When you take damage, someone else",
        "§7takes the damage instead of you"
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player ||
            !(event.entity as Player).isPlaying() ||
            event.cause == EntityDamageEvent.DamageCause.CUSTOM
        ) {
            return
        }
        val otherPlayers = onlinePlayers.filter { it != event.entity && it.isPlaying() }
        if (otherPlayers.isEmpty()) {
            return
        }
        otherPlayers
            .random()
            .damage(event.damage)
        event.isCancelled = true
    }
}
