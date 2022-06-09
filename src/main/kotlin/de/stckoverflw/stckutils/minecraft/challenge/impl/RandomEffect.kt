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
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.Locale

object RandomEffect : Challenge() {

    override val id: String = "random-effect"
    override val material: Material = Material.POTION
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) {
            return
        }
        val player = event.entity as Player
        if (!player.isPlaying() ||
            event.cause == EntityDamageEvent.DamageCause.POISON ||
            event.cause == EntityDamageEvent.DamageCause.WITHER
        ) {
            return
        }

        val potionTypes = PotionEffectType.values()
        val potionEffectType = potionTypes.random()

        onlinePlayers.forEach {
            it.addPotionEffect(
                PotionEffect(
                    potionEffectType,
                    Integer.MAX_VALUE,
                    it.getPotionEffect(potionEffectType)?.amplifier?.plus(1) ?: 0
                )
            )
        }
    }
}
