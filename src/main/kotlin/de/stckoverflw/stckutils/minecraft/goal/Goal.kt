package de.stckoverflw.stckutils.minecraft.goal

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.minecraft.timer.Timer
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.*
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
    fun win(id: String, replacements: Array<Any?> = arrayOf()) {
        if (Timer.running) {
            spawnFireworks()
            onlinePlayers.forEach {
                it.sendMessage(
                    StckUtilsPlugin.translationsProvider.translateWithPrefix(
                        "team_goal.win",
                        it.language,
                        id,
                        arrayOf(replacements, ChatColor.stripColor(Timer.formatTime()))
                    )
                )
            }
            Timer.stop()
        }
    }
}

abstract class Battle : Goal() {
    fun win(player: Player, id: String, replacements: Array<Any?> = arrayOf()) {
        if (Timer.running) {
            spawnFireworks()
            onlinePlayers.forEach {
                it.sendMessage(
                    StckUtilsPlugin.translationsProvider.translateWithPrefix(
                        "battle.win",
                        it.language,
                        id,
                        arrayOf(replacements, player.name, ChatColor.stripColor(Timer.formatTime()))
                    )
                )
            }
            Timer.stop()
        }
    }
}

private fun spawnFireworks() {
    onlinePlayers.forEach {
        val loc = it.location
        val firework = loc.world.spawnEntity(loc, EntityType.FIREWORK) as Firework
        val fireworkMeta = firework.fireworkMeta

        fireworkMeta.power = 1
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.BLUE).flicker(true).build())

        firework.fireworkMeta = fireworkMeta
        firework.detonate()

        firework.fireworkMeta = fireworkMeta
        it.gameMode = GameMode.SPECTATOR
    }
}
