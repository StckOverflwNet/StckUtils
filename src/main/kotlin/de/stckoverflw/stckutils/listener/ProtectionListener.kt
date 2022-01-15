package de.stckoverflw.stckutils.listener

import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.getSettingsItem
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class ProtectionListener : Listener {

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        if (!Timer.running && event.player.isOp) {
            event.player.inventory.setItem(8, getSettingsItem(event.player.locale()))
        }
    }

    @EventHandler
    fun onItemPickUp(event: PlayerAttemptPickupItemEvent) {
        event.isCancelled = !Timer.running
    }

    @EventHandler
    fun onEntityTarget(event: EntityTargetEvent) {
        event.isCancelled = !Timer.running
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        event.isCancelled = !Timer.running
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        event.isCancelled = !Timer.running
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        event.isCancelled = !Timer.running
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        event.isCancelled = !Timer.running
    }

    @EventHandler
    fun onSwapHand(event: PlayerSwapHandItemsEvent) {
        event.isCancelled = !Timer.running
    }

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        event.isCancelled = !Timer.running
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        event.isCancelled = !Timer.running
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        if (event.message.split(" ").isEmpty()) return
        if (!Timer.running) {
            if (player.isOp) {
                val command = event.message.split(" ")[0].replace("/", "")
                if (event.message.split(" ").size > 1) {
                    val target = Bukkit.getPlayer(event.message.split(" ")[1])
                    if (target != null) {
                        if (command == "op") {
                            target.inventory.setItem(8, getSettingsItem(target.locale()))
                        } else if (command == "deop") {
                            target.inventory.setItem(8, null)
                            target.closeInventory()
                        }
                    }
                }
            }
        }
    }
}
