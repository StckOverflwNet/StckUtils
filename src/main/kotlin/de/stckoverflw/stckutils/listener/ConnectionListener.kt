package de.stckoverflw.stckutils.listener

import de.stckoverflw.stckutils.extension.*
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.Namespaces
import de.stckoverflw.stckutils.util.get
import de.stckoverflw.stckutils.util.set
import de.stckoverflw.stckutils.util.settingsItem
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

class ConnectionListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (player.persistentDataContainer.get(Namespaces.CHALLENGE_FUNCTION_HIDDEN) == 1.toByte()
        ) player.hide() else player.reveal()
        player.inventory.clear()
        if (!Timer.running) {
            event.joinMessage(Component.text("§7[§a+§7]§7 ${player.name}"))
            if (player.isOp) {
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
        if (!Timer.running && player.isPlaying()) {
            event.quitMessage(Component.text("§7[§c-§7]§7 ${player.name}"))
        } else if (Timer.running && player.isPlaying()) {
            player.persistentDataContainer.set(
                Namespaces.CHALLENGE_INVENTORY_CONTENTS,
                toBase64(player.inventory.contents as Array<ItemStack>)
            )
            player.inventory.clear()
            event.quitMessage(null)
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
            if (player.isOp || player.persistentDataContainer.get(Namespaces.CHALLENGE_FUNCTION_HIDDEN) == 1.toByte()) {
                player.gameMode = GameMode.SPECTATOR
                player.sendMessage("§cThe Timer is currently running, you were put in spectator mode")
            } else {
                event.disallow(
                    PlayerLoginEvent.Result.KICK_OTHER,
                    Component.text("§cThe Timer is currently running, you can't join at the moment.")
                )
            }
        }
    }
}
