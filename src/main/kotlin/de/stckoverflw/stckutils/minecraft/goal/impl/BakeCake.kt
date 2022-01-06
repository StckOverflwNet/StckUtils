package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.minecraft.goal.Battle
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent

object BakeCake : Battle() {

    override val id: String = "bake-cake"
    override val material: Material = Material.CAKE

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        if (event.currentItem!!.type == Material.CAKE) {
            win(event.whoClicked as Player, id, arrayOf(event.whoClicked.name))
        }
    }
}
