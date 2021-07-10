package de.stckoverflw.stckutils.listener

import de.stckoverflw.stckutils.user.settingsGUI
import de.stckoverflw.stckutils.user.settingsItem
import net.axay.kspigot.gui.openGUI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class InteractListener : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.player.inventory.itemInMainHand.isSimilar(settingsItem)) {
                event.player.openGUI(settingsGUI())
            }
        }
    }
}