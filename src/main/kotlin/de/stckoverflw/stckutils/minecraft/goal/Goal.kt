package de.stckoverflw.stckutils.minecraft.goal

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.minecraft.timer.Timer
import net.axay.kspigot.extensions.onlinePlayers
import net.kyori.adventure.text.Component
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
     * the name of the goal, the item in the inventory is called like this
     */
    abstract val name: String

    /**
     * the description of the goal, the item in the inventory has this as lore
     */
    abstract val description: List<String>

    /**
     * the item of the goal, this is used in the challenge inventory
     */
    abstract val material: Material

    open fun onTimerToggle() {
    }

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
    fun win(reason: String) {
        if (Timer.running) {
            spawnFireworks()
            Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + reason))
            Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7You finished the Challenge §asucessfully"))
            Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7Time needed: §9" + ChatColor.stripColor(Timer.formatTime())))
            Timer.stop()
        }
    }
}

abstract class Battle : Goal() {
    fun win(player: Player, reason: String) {
        if (Timer.running) {
            spawnFireworks()
            Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + reason))
            Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§9${player.name} §awon §7the Challenge"))
            Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7Time needed: §9" + ChatColor.stripColor(Timer.formatTime())))
            Timer.stop()
        }
    }
}

private fun spawnFireworks() {
    onlinePlayers.forEach {
        val loc = it.location
        val fw = loc.world.spawnEntity(loc, EntityType.FIREWORK) as Firework
        val fwm = fw.fireworkMeta

        fwm.power = 1
        fwm.addEffect(FireworkEffect.builder().withColor(Color.BLUE).flicker(true).build())

        fw.fireworkMeta = fwm
        fw.detonate()

        fw.fireworkMeta = fwm
        it.gameMode = GameMode.SPECTATOR
    }
}
