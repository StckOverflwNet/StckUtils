package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.minecraft.goal.TeamGoal
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent

object FindDiamond : TeamGoal() {

    override val id: String = "find-diamond"
    override val material: Material = Material.DIAMOND

    @EventHandler
    fun onPickup(event: PlayerAttemptPickupItemEvent) {
        if (event.item.itemStack.type == Material.DIAMOND) {
            win(listOf(event.player.name()))
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.currentItem == null) return
        if (event.currentItem!!.type == Material.DIAMOND) {
            win(listOf(event.whoClicked.name()))
        }
    }
}
