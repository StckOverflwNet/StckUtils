package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.minecraft.goal.Goal
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChangedWorldEvent

object GoToNether : Goal() {
    override val id: String = "go-to-nether"
    override val name: String = "§6Go to the Nether"
    override val description: List<String> = listOf(
        " ",
        "§7The Challenge is finished when someone",
        "§6goes to the Nether"
    )
    override val material: Material = Material.NETHERRACK

    @EventHandler
    fun onWorldSwitch(event: PlayerChangedWorldEvent) {
        if (event.player.gameMode != GameMode.SPECTATOR) {
            if (event.player.world.name == "world_nether") {
                win("§9" + event.player.name + " §7went to the §6Nether")
            }
        }
    }
}
