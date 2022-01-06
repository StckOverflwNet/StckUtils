package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

object DamageFreeze : Challenge() {

    override val id: String = "damage-freeze"
    override val material: Material = Material.ICE
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player ||
            !(event.entity as Player).isPlaying()
        ) {
            return
        }
        val player = event.entity as Player
        var duration = (event.finalDamage / 2 * 60 * 20).toInt()
        if (player.hasPotionEffect(PotionEffectType.SLOW) &&
            player.getPotionEffect(PotionEffectType.SLOW)!!.amplifier == 100000
        ) {
            duration += player.getPotionEffect(PotionEffectType.SLOW)!!.duration
        }
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, duration, 100000, false, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, duration, 100000))
    }
}
