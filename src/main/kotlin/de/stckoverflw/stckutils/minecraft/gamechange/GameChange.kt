package de.stckoverflw.stckutils.minecraft.gamechange

import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

sealed class GameChange : Listener {
    /**
     * this is the id of the challenge, it has to be unique
     * it is used to get the information about the gamechange from the gamechanges.yml
     */
    abstract val id: String

    /**
     * the item that is shown in the GUI
     */
    abstract val item: ItemStack

    /**
     * If the Challenge uses Event(s) register the class as a Listener
     */
    abstract val usesEvents: Boolean

    /**
     * Gets called when someone clicks the Item for the GameChange in the Inventory
     */
    abstract fun click(event: GUIClickEvent<ForInventoryFiveByNine>)

    /**
     * This method is run everytime someone joins the server and when the gamechange gets updated
     */
    abstract fun run()
}

abstract class GameExtension: GameChange()

abstract class GameRule: GameChange()