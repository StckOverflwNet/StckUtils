package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.minecraft.goal.TeamGoal
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChangedWorldEvent

object GoToNether : TeamGoal() {

    override val id: String = "go-to-nether"
    override val material: Material = Material.NETHERRACK

    @EventHandler
    fun onWorldSwitch(event: PlayerChangedWorldEvent) {
        if (event.player.gameMode != GameMode.SPECTATOR) {
            if (event.player.world.environment == World.Environment.NETHER) {
                win(id, arrayOf(event.player.name))
            }
        }
    }
}
