package de.stckoverflw.stckutils.listener

import de.stckoverflw.stckutils.timer.Timer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class ProtectionListener : Listener {

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
}