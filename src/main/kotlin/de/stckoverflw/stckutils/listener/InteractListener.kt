package de.stckoverflw.stckutils.listener

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.util.Permissions
import de.stckoverflw.stckutils.util.getSettingsItem
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.gui.openGUI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class InteractListener : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.player.inventory.itemInMainHand.isSimilar(getSettingsItem(event.player.language))) {
                if (!event.player.hasPermission(Permissions.SETTINGS_GUI)) {
                    return event.player.sendMessage(StckUtilsPlugin.prefix + "§cMissing permission: ${Permissions.SETTINGS_GUI}")
                }
                event.player.openGUI(settingsGUI(event.player.language))
            }
        }
    }
}
