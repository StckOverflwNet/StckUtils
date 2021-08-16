package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.minecraft.goal.Battle
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent

object BakeCake : Battle() {

    override val id: String = "bake-cake"
    override val name: String = "§fBake a Cake"
    override val description: List<String> = listOf(
        " ",
        "§7The first one to bake a §fCake",
        "§7wins the Challenge",
        "§c§oKeep in mind that just crafting",
        "§c§ois counted, if you find one it",
        "§c§owill not count as a win!"
    )
    override val material: Material = Material.CAKE

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        if (event.currentItem!!.type == Material.CAKE) {
            win(event.whoClicked as Player, "§9${(event.whoClicked as Player).name} §7crafted a §7Cake§7!")
        }
    }
}
