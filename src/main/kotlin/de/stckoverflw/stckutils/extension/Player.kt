package de.stckoverflw.stckutils.extension

import de.stckoverflw.stckutils.config.Config
import net.axay.kspigot.extensions.onlinePlayers
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun Player.resetWorlds() {
    Config.resetSettings.shouldReset = true
    onlinePlayers.forEach {
        it.kick(
            Component.join(
                Component.newline(),
                Component.text("§7The World is §cresetting"),
                Component.text("§7World reset started by §9${name}")
            )
        )
    }
    Bukkit.getServer().spigot().restart()
}