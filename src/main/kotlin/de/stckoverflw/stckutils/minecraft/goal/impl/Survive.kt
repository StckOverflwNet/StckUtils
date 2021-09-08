package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.minecraft.goal.Battle
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent

object Survive : Battle() {
    override val id: String = "survive-longer"
    override val name: String = "§bSurvive longer"
    override val description: List<String> = listOf(
        " ",
        "§7The §7§llast §7Person to survive",
        "§awins §7the Challenge"
    )
    override val material: Material = Material.BONE

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        player.gameMode = GameMode.SPECTATOR
        player.sendMessage(StckUtilsPlugin.prefix + "§cYou died! You lost the Battle")
        val alivePlayers = onlinePlayers.filter { it.gameMode == GameMode.SURVIVAL }
        if (alivePlayers.size == 1) {
            val winningPlayer = alivePlayers[0]
            win(winningPlayer, "§9${winningPlayer.name} §7was the §alast §7Player to §asurvive")
        }
    }
}
