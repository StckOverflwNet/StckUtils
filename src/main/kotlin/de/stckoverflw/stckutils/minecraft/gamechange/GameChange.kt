package de.stckoverflw.stckutils.minecraft.gamechange

import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.Listener

abstract class GameChange : Listener {

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
     * the description of the challenge, the item in the inventory is called like this
     */
    abstract val description: List<String>

    /**
     * the item of the gamechange, this is used in the challenge inventory
     */
    abstract val material: Material

    /**
     * If the Challenge uses Event(s) register the class as a Listener
     */
    abstract val usesEvents: Boolean

    /**
     * The GUI for changing settings for that challenge
     */
    abstract fun configurationGUI(): GUI<ForInventoryFiveByNine>?

    /**
     * Whether the Game Change is activated by default
     */
    abstract val defaultActivated: Boolean

    /**
     * This method is run everytime someone joins the server and when the gamechange gets updated
     */
    abstract fun run()
}

var GameChange.active: Boolean
    get() = GameChangeManager.gameChanges.getOrDefault(this, false)
    set(value) = GameChangeManager.gameChanges.set(this, value)
