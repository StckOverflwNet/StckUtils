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
        val player = event.player
        val action = event.action
        if (
            (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) &&
            player.inventory.itemInMainHand.isSimilar(getSettingsItem(player.language))
        ) {
            if (!player.hasPermission(Permissions.SETTINGS_GUI)) {
                return player.sendMessage(
                    StckUtilsPlugin.translationsProvider.translateWithPrefix(
                        "generic.missing_permission",
                        player.language,
                        "general",
                        arrayOf(Permissions.SETTINGS_GUI)
                    )
                )
            }
            player.openGUI(settingsGUI(player.language))
        }
    }
}
