package de.stckoverflw.stckutils.listener

import de.stckoverflw.stckutils.extension.*
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.timer.AccessLevel
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.Permissions
import de.stckoverflw.stckutils.util.settingsItem
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class ConnectionListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        // hide functionality
        if (player.hidden) {
            player.hide()
        } else {
            player.reveal()
        }

        // set inventory
        player.inventory.clear()
        if (!Timer.running) {
            event.joinMessage(Component.text("§7[§a+§7]§7 ${player.name}"))
            if (player.hasPermission(Permissions.SETTINGS_ITEM)) {
                player.inventory.setItem(8, settingsItem)
            }
        } else {
            player.setSavedInventory()
            event.joinMessage(null)
        }

        // notify gamechanges
        GameChangeManager.gameChanges.forEach { change ->
            change.run()
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        if (!Timer.running) {
            event.quitMessage(Component.text("§7[§c-§7]§7 ${player.name}"))
        } else {
            player.saveInventory()
            player.inventory.clear()
            event.quitMessage(null)
        }

        // notify gamechanges
        GameChangeManager.gameChanges.forEach { change ->
            change.run()
        }
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        val player = event.player
        if (Timer.running) {
            var disallow = false
            Timer.joinWhileRunning.forEach { accessLevel ->
                when (accessLevel) {
                    AccessLevel.OPERATOR -> {
                        if (player.isOp) {
                            disallow = false
                            return@forEach
                        }
                        disallow = true
                    }
                    AccessLevel.HIDDEN -> {
                        if (player.hidden) {
                            disallow = false
                            return@forEach
                        }
                        disallow = true
                    }
                    // other AccessLevels
                    AccessLevel.EVERYONE -> return@forEach
                    AccessLevel.NONE -> {
                        disallow = true
                        return@forEach
                    }
                }
            }

            if (disallow) {
                event.disallow(
                    PlayerLoginEvent.Result.KICK_OTHER,
                    Component.text(
                        """§cThe Timer is currently running, you can't join at the moment.
                                    |Ask an Operator to change the setting if you believe that this isn't intended.
                            """.trimMargin()
                    )
                )
            }
        }
        player.gameMode = GameMode.SPECTATOR
        player.sendMessage("§cThe Timer is currently running, you were put in spectator mode")
    }
}
