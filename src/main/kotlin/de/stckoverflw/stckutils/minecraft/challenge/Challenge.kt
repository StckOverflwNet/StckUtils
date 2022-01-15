package de.stckoverflw.stckutils.minecraft.challenge

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.minecraft.timer.Timer
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.main.KSpigotMainInstance
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.Listener
import java.util.Locale

abstract class Challenge(val requiresProtocolLib: Boolean = false) : Listener {

    /**
     * this is the id of the challenge, it has to be unique
     * it is used to get the information about the challenge from the (data) configs
     */
    abstract val id: String

    /**
     * the item of the challenge, this is used in the challenge inventory
     */
    abstract val material: Material

    /**
     * If the Challenge uses Event(s) register the class as a Listener
     */
    abstract val usesEvents: Boolean

    /**
     * The GUI for changing settings for that challenge
     */
    abstract fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>?

    /**
     * Is run before the Timer starts to prepare the Challenge
     */
    open fun prepareChallenge() {
        KSpigotMainInstance.logger.info("Preparing Challenge")
    }

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

    /**
     * Is run every second synchronously with the timer
     */
    open fun update() {
    }

    /**
     * Is run once a challenge is lost
     */
    fun lose(replacements: List<Component> = listOf()) {
        Timer.stop()
        Bukkit.getOnlinePlayers().forEach {
            it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 0.5F, 1F)
            it.sendMessage(
                translatable(
                    "challenge.lose",
                    listOf(
                        translatable(loseKey, replacements),
                        text(Timer.toString())
                    )
                )
            )
        }
        Timer.backwardsStartTime = 0
    }
}

val Challenge.nameKey: String
    get() = "$id.name"
val Challenge.descriptionKey: String
    get() = "$id.description"
val Challenge.loseKey: String
    get() = "$id.lose"

var Challenge.active: Boolean
    get() = Config.challengeConfig.getActive(this.id)
    set(value) = Config.challengeConfig.setActive(this.id, value)
