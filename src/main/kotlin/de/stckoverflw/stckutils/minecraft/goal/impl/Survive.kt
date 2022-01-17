package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.extension.sendPrefixMessage
import de.stckoverflw.stckutils.minecraft.goal.Battle
import net.axay.kspigot.extensions.onlinePlayers
import net.kyori.adventure.text.Component.translatable
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent

object Survive : Battle() {

    override val id: String = "survive-longer"
    override val material: Material = Material.BONE

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        player.gameMode = GameMode.SPECTATOR
        player.sendPrefixMessage(
            translatable("$id.died")
        )
        val alivePlayers = onlinePlayers.filter { it.gameMode == GameMode.SURVIVAL }
        if (alivePlayers.size == 1) {
            val winningPlayer = alivePlayers[0]
            win(winningPlayer, listOf(winningPlayer.name()))
        }
    }
}
