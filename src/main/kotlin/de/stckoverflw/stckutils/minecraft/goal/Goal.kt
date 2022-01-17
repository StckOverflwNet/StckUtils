package de.stckoverflw.stckutils.minecraft.goal

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.sendPrefixMessage
import de.stckoverflw.stckutils.minecraft.timer.Timer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.bukkit.bukkitColor
import net.axay.kspigot.extensions.onlinePlayers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.ChatColor
import org.bukkit.FireworkEffect
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.Listener

sealed class Goal : Listener {

    /**
     * this is the id of the goal, it has to be unique
     */
    abstract val id: String

    /**
     * the item of the goal, this is used in the challenge inventory
     */
    abstract val material: Material

    /**
     * Is run when the Timer is toggled (start/stop, not reset)
     */
    open fun onTimerToggle() {
    }

    /**
     * Is run when the Item in the settings gui is clicked and the state is changed
     */
    open fun onToggle() {
    }
}

var Goal.active: Boolean
    get() = Config.goalConfig.getActive(this.id)
    set(value) {
        Config.goalConfig.setActive(this.id, value)
        this.onToggle()
    }

abstract class TeamGoal : Goal() {
    fun win(replacements: List<Component> = listOf()) {
        if (Timer.running) {
            spawnFireworks()
            onlinePlayers.forEach {
                it.sendPrefixMessage(
                    translatable(
                        "team_goal.win",
                        listOf(
                            translatable(winKey, replacements),
                            text(ChatColor.stripColor(Timer.toString())!!)
                        )
                    )
                )
                Timer.stop()
            }
        }
    }
}

abstract class Battle : Goal() {
    fun win(player: Player, replacements: List<Component> = listOf()) {
        if (Timer.running) {
            spawnFireworks()
            onlinePlayers.forEach {
                it.sendPrefixMessage(
                    translatable(
                        "battle.win",
                        listOf(
                            translatable(winKey, replacements),
                            player.name(),
                            text(ChatColor.stripColor(Timer.toString())!!)
                        )
                    )
                )
            }
            Timer.stop()
        }
    }
}

val Goal.nameKey: String
    get() = "$id.name"
val Goal.descriptionKey: String
    get() = "$id.description"
val Goal.winKey: String
    get() = "$id.win"

private fun spawnFireworks() {
    onlinePlayers.forEach {
        val loc = it.location
        val firework = loc.world.spawnEntity(loc, EntityType.FIREWORK) as Firework
        val fireworkMeta = firework.fireworkMeta

        fireworkMeta.power = 1
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(KColors.BLUE.bukkitColor).flicker(true).build())

        firework.fireworkMeta = fireworkMeta
        firework.detonate()

        firework.fireworkMeta = fireworkMeta
        it.gameMode = GameMode.SPECTATOR
    }
}
