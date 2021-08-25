package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.minecraft.goal.TeamGoal
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent

object FindDiamond : TeamGoal() {

    override val id: String = "find-diamond"
    override val name: String = "§bFind a Diamond"
    override val description: List<String> = listOf(
        " ",
        "§7The Challenge is finished when someone",
        "§bfinds a Diamond",
    )
    override val material: Material = Material.DIAMOND

    @EventHandler
    fun onPickup(event: PlayerAttemptPickupItemEvent) {
        if (event.item.itemStack.type == Material.DIAMOND) {
            win("§9" + event.player.name + "§7 found a §bDiamond")
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.currentItem == null) return
        if (event.currentItem!!.type == Material.DIAMOND) {
            win("§9" + event.whoClicked.name + "§7 found a §bDiamond")
        }
    }
}
