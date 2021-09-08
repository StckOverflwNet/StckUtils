package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.config.data.PositionData
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class PositionCommand : TabExecutor {

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        return if (args.isNotEmpty()) {
            Config.positionConfig.positions.map { it.name }.filter { it.startsWith(args[0], true) }.sorted()
        } else {
            emptyList()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender
            if (args.isNotEmpty()) {
                if (!Config.positionConfig.positions.any { it.name == args[0].lowercase() }) {
                    val location = player.location
                    Config.positionConfig.addPosition(
                        PositionData(
                            args[0].lowercase(),
                            player.uniqueId,
                            location
                        )
                    )
                    broadcast(StckUtilsPlugin.prefix + "§9${player.name} §7found §9${args[0].lowercase()} §7at [§9${location.blockX}§7,§9${location.blockY}§7,§9${location.blockZ}§7]")
                } else {
                    val position = Config.positionConfig.positions.find {
                        it.name == args[0]
                    }
                    if (position != null) {
                        val location = position.location
                        player.sendMessage(StckUtilsPlugin.prefix + "§9${position.name} §7by §9${player.name} §7is at [§9${location.blockX}§7,§9${location.blockY}§7,§9${location.blockZ}§7]")
                    }
                }
            }
        }
        return true
    }
}