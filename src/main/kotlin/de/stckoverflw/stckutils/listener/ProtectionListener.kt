package de.stckoverflw.stckutils.listener

import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.user.settingsItem
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*

class ProtectionListener : Listener {

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        if (!Timer.running && event.player.isOp) {
            event.player.inventory.setItem(8, settingsItem)
        }
    }

    @EventHandler
    fun onItemPickUp(event: PlayerAttemptPickupItemEvent) {
        if (!Timer.running) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityTarget(event: EntityTargetEvent) {
        if (!Timer.running) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!Timer.running) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!Timer.running) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (!Timer.running) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (!Timer.running) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onSwapHand(event: PlayerSwapHandItemsEvent) {
        if (!Timer.running) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (!Timer.running) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        if (!Timer.running) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        if (!Timer.running) {
            if (player.isOp) {
                val command = event.message.split(" ")[0].replace("/", "")
                if (event.message.split(" ").size > 1) {
                    val target = Bukkit.getPlayer(event.message.split(" ")[1])
                    if (target != null) {
                        if (command == "op") {
                            target.inventory.setItem(8, settingsItem)
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
