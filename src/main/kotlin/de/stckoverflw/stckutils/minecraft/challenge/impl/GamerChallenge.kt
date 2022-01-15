package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.timer.Timer
import net.axay.kspigot.extensions.bukkit.kill
import net.axay.kspigot.extensions.geometry.minus
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.runnables.task
import org.bukkit.Material
import org.bukkit.util.Vector
import java.util.Locale
import java.util.UUID

object GamerChallenge : Challenge() {

    private val respawned = HashMap<UUID, Boolean>()

    override val id: String = "no-grass"
    override val material: Material = Material.GRASS
    override val usesEvents: Boolean = false

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    override fun prepareChallenge() {
        onlinePlayers.forEach { player ->
            respawned[player.uniqueId] = true
        }
        task(
            sync = true,
            delay = 5,
            period = 1
        ) {
            if (Timer.running) {
                onlinePlayers.forEach { player ->
                    if (!player.isPlaying()) return@forEach
                    if (
                        player.location.clone().minus(Vector(0.0, 0.25, 0.0)).block.type == Material.GRASS_BLOCK
                    ) {
                        if (respawned.containsKey(player.uniqueId)) {
                            if (respawned[player.uniqueId]!!) {
                                return@forEach
                            }
                        }
                        player.kill()
                        respawned[player.uniqueId] = true
                    } else {
                        if (player.location.clone().minus(Vector(0.0, 0.25, 0.0)).block.isSolid) {
                            if (respawned.containsKey(player.uniqueId)) {
                                if (respawned[player.uniqueId]!!) {
                                    respawned[player.uniqueId] = false
                                }
                            }
                        }
                    }
                }
            } else {
                it.cancel()
            }
        }
    }
}
