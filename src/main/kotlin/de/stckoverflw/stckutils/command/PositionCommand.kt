package de.stckoverflw.stckutils.command

import com.mojang.brigadier.arguments.StringArgumentType
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.config.data.PositionData
import net.axay.kspigot.commands.*
import net.axay.kspigot.extensions.broadcast

class PositionCommand {

    fun register() = command("position", true) {
        argument("name", StringArgumentType.string()) {
            suggestListSuspending { suggest ->
                Config.positionDataConfig.positions.map { it.name }.filter {
                    if (suggest.input != null && suggest.input.substring(suggest.input.length - 1) != " ")
                        it.startsWith(suggest.getArgument<String>("name"), true) else
                        true
                }.sorted()
            }
            runs {
                if (!Config.positionDataConfig.positions.any { it.name == getArgument<String>("name").lowercase() }) {
                    val location = player.location
                    Config.positionDataConfig.addPosition(
                        PositionData(
                            getArgument<String>("name").lowercase(),
                            player.uniqueId,
                            location
                        )
                    )
                    broadcast(StckUtilsPlugin.prefix + "§9${player.name} §7found §9${getArgument<String>("name").lowercase()} §7at [§9${location.blockX}§7,§9${location.blockY}§7,§9${location.blockZ}§7]")
                } else {
                    val position = Config.positionDataConfig.positions.find {
                        it.name == getArgument<String>("name")
                    }
                    if (position != null) {
                        val location = position.location
                        player.sendMessage(StckUtilsPlugin.prefix + "§9${position.name} §7by §9${player.name} §7is at [§9${location.blockX}§7,§9${location.blockY}§7,§9${location.blockZ}§7]")
                    }
                }
            }
        }
    }
}
