package de.stckoverflw.stckutils.listener

import de.stckoverflw.stckutils.extension.hide
import de.stckoverflw.stckutils.extension.reveal
import de.stckoverflw.stckutils.extension.saveInventory
import de.stckoverflw.stckutils.extension.setSavedInventory
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.timer.AccessLevel
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.Namespaces
import de.stckoverflw.stckutils.util.get
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
        if (player.persistentDataContainer.get(Namespaces.CHALLENGE_FUNCTION_HIDDEN) == 1.toByte()
        ) player.hide() else player.reveal()
        player.inventory.clear()
        if (!Timer.running) {
            event.joinMessage(Component.text("§7[§a+§7]§7 ${player.name}"))
            if (player.hasPermission("stckutils.feature.settings")) {
                player.inventory.setItem(8, settingsItem)
            }
        } else {
            player.setSavedInventory()
            event.joinMessage(null)
        }
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
        GameChangeManager.gameChanges.forEach { change ->
            change.run()
        }
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        val player = event.player
        if (Timer.running) {
            when (Timer.joinWhileRunning) {
                AccessLevel.OPERATOR -> {
                    if (!player.isOp) {
                        event.disallow(
                            PlayerLoginEvent.Result.KICK_OTHER,
                            Component.text(
                                """§cThe Timer is currently running, you can't join at the moment.
                                    |Ask an Operator to change the setting if you believe that this isn't intended.
                            """.trimMargin()
                            )
                        )
                        return
                    }
                }
                AccessLevel.HIDDEN -> {
                    if (player.persistentDataContainer.get(Namespaces.CHALLENGE_FUNCTION_HIDDEN) != 1.toByte()) {
                        event.disallow(
                            PlayerLoginEvent.Result.KICK_OTHER,
                            Component.text(
                                """§cThe Timer is currently running, you can't join at the moment.
                                    |Ask an Operator to change the setting if you believe that this isn't intended.
                            """.trimMargin()
                            )
                        )
                    }
                    return
                }
                AccessLevel.EVERYONE -> {
                    // allow join
                }
                AccessLevel.NONE -> {
                    event.disallow(
                        PlayerLoginEvent.Result.KICK_OTHER,
                        Component.text(
                            """§cThe Timer is currently running, you can't join at the moment.
                                    |Ask an Operator to change the setting if you believe that this isn't intended.
                            """.trimMargin()
                        )
                    )
                    return
                }
            }
            player.gameMode = GameMode.SPECTATOR
            player.sendMessage("§cThe Timer is currently running, you were put in spectator mode")
        }
    }
}
