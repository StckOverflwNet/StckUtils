package de.stckoverflw.stckutils.minecraft.challenge

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.minecraft.timer.Timer
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.main.KSpigotMainInstance
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.Listener

abstract class Challenge : Listener {

    /**
     * this is the id of the challenge, it has to be unique
     * it is used to get the information about the challenge from the challenges.json
     */
    abstract val id: String

    /**
     * the name of the challenge, the item in the inventory is called like this
     */
    abstract val name: String

    /**
     * the item of the challenge, this is used in the challenge inventory
     */
    abstract val material: Material

    /**
     * the description of the challenge, the item in the inventory is called like this
     */
    abstract val description: List<String>

    /**
     * If the Challenge uses Event(s) register the class as a Listener
     */
    abstract val usesEvents: Boolean

    /**
     * The GUI for changing settings for that challenge
     */
    abstract fun configurationGUI(): GUI<ForInventoryFiveByNine>?

    /**
     * Is run before the Timer starts to prepare the Challenge
     */
    open fun prepareChallenge() {
        KSpigotMainInstance.logger.info("§aPreparing Challenge")
    }

    open fun onToggle() {
    }

    /**
     * Is run every second synchronously with the timer
     */
    open fun update() {
    }

    /**
     * Is run once a challenge is lost
     */
    fun lose(reason: String) {
        Timer.stop()
        Bukkit.getOnlinePlayers().forEach {
            it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 0.5F, 1F)
        }

        Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7You §cfailed §7the Challenge!"))
        Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + reason))
        Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7Time wasted: $Timer"))
    }
}

var Challenge.active: Boolean
    get() = Config.challengeConfig.getActive(this.id)
    set(value) = Config.challengeConfig.setActive(this.id, value)
