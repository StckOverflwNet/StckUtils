package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*

object BalanceLife : Challenge() {

    private var isFirstRun
        get() = (Config.challengeConfig.getSetting(MobDuplicator.id, "isFirstRun") ?: true) as Boolean
        set(value) = Config.challengeConfig.setSetting(MobDuplicator.id, "isFirstRun", value)

    override val id: String = "balance-life"
    override val material: Material = Material.HEART_OF_THE_SEA
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    override fun prepareChallenge() {
        if (isFirstRun) {
            onlinePlayers.forEach { player ->
                player.health = (player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value?.div(2)) as Double
                player.foodLevel = 10
            }
            isFirstRun = false
        }
    }

    @EventHandler
    fun onHealthRegain(event: EntityRegainHealthEvent) {
        if (event.entity !is Player) {
            return
        }
        val player = event.entity as Player
        val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        if (player.isPlaying() && player.health + event.amount >= maxHealth?.value!!) {
            lose(id, arrayOf(player.name, maxHealth.value.toInt()))
            isFirstRun = true
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        if (!event.entity.isPlaying()) {
            return
        }
        lose(id, arrayOf(event.entity.name, 0))
        isFirstRun = true
    }
}
