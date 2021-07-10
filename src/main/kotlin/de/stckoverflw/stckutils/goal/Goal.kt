package de.stckoverflw.stckutils.goal

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.timer.Timer
import net.axay.kspigot.extensions.onlinePlayers
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.Listener

abstract class Goal : Listener {

    /**
     * this is the id of the goal, it has to be unique
     * it is used to get the information about the challenge from the challenges.json
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

    fun win(reason: String) {
        if (Timer.running) {
            Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + reason))
            Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7You finished the Challenge §asucessfully"))
            Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7Time needed: §9" + ChatColor.stripColor(Timer.toString())))
            Timer.stop()
        }
    }
}