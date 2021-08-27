package de.stckoverflw.stckutils.listener

import de.stckoverflw.stckutils.extension.saveInventory
import de.stckoverflw.stckutils.extension.setSavedInventory
import de.stckoverflw.stckutils.extension.toBase64
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.user.settingsItem
import net.axay.kspigot.main.KSpigotMainInstance
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ConnectionListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
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
        if (!Timer.running && player.gameMode != GameMode.SPECTATOR) {
            event.quitMessage(Component.text("§7[§c-§7]§7 ${player.name}"))
        } else if (Timer.running && player.gameMode != GameMode.SPECTATOR) {
            player.persistentDataContainer.set(
                NamespacedKey(KSpigotMainInstance, "challenge-inventory-contents"),
                PersistentDataType.STRING,
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
            if (player.isOp) {
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
