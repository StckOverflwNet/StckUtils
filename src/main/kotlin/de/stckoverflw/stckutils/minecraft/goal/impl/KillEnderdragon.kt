package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.minecraft.goal.TeamGoal
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent

object KillEnderdragon : TeamGoal() {

    override val id: String = "kill-dragon"
    override val material: Material = Material.DRAGON_HEAD

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (event.entityType == EntityType.ENDER_DRAGON) {
            win()
        }
    }
}
