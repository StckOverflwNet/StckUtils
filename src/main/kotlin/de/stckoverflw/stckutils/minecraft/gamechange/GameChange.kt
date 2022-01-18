package de.stckoverflw.stckutils.minecraft.gamechange

import de.stckoverflw.stckutils.config.Config
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.Locale

sealed class GameChange : Listener {

    /**
     * this is the id of the challenge, it has to be unique
     * it is used to get the information about the gamechange from the gamechanges.yml
     */
    abstract val id: String

    /**
     * the item that is shown in the GUI
     */
    abstract fun item(locale: Locale): ItemStack

    /**
     * If the Challenge uses Event(s) register the class as a Listener
     */
    abstract val usesEvents: Boolean

    /**
     * Gets called when someone clicks the Item for the GameChange in the Inventory
     */
    abstract fun click(event: GUIClickEvent<ForInventoryFiveByNine>)

    /**
     * This method is run everytime someone joins or quits the server and when the timer starts
     */
    abstract fun run()

    /**
     * Is run when the Timer is toggled (start/stop, not reset)
     */
    open fun onTimerToggle() {
    }
}

val GameChange.nameKey: String
    get() = "$id.name"
val GameChange.descriptionKey: String
    get() = "$id.description"

var GameChange.active: Boolean
    get() = Config.gameChangeConfig.getActive(this.id)
    set(value) = Config.gameChangeConfig.setActive(this.id, value)

abstract class GameExtension : GameChange()

abstract class GameRule : GameChange()
