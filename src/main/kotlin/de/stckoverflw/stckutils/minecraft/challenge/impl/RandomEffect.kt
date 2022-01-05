package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

object RandomEffect : Challenge() {

    override val id: String = "random-effect"
    override val name: String = "§dRa§bnd§aom §eEf§3fe§6ct"
    override val material: Material = Material.POTION
    override val description: List<String> = listOf(
        " ",
        "§7Every time you get damage every player is getting a random effect.",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) {
            return
        }
        val player = event.entity as Player
        if (!player.isPlaying()) {
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.POISON || event.cause == EntityDamageEvent.DamageCause.WITHER) {
            return
        }

        val potionTypes = PotionEffectType.values()
        val potionEffectType = potionTypes.random()

        Bukkit.getOnlinePlayers().forEach {
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
