package de.stckoverflw.stckutils.command

import com.mojang.brigadier.arguments.StringArgumentType
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.config.data.PositionData
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.util.Permissions
import net.axay.kspigot.commands.*
import net.axay.kspigot.extensions.onlinePlayers

class PositionCommand {

    fun register() = command("position", true) {
        requiresPermission(Permissions.POSITION_COMMAND)
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
                    requiresPermission(Permissions.POSITION_CREATE)
                    val location = player.location
                    val name = getArgument<String>("name").lowercase()
                    Config.positionDataConfig.addPosition(
                        PositionData(
                            name,
                            player.uniqueId,
                            location
                        )
                    )
                    onlinePlayers.forEach {
                        it.sendMessage(
                            StckUtilsPlugin.translationsProvider.translateWithPrefix(
                                "position.create",
                                it.language,
                                "messages",
                                arrayOf(player.name, name, location.blockX, location.blockY, location.blockZ)
                            )
                        )
                    }
                } else {
                    requiresPermission(Permissions.POSITION_SHOW)
                    val position = Config.positionDataConfig.positions.find {
                        it.name == getArgument<String>("name")
                    }
                    if (position != null) {
                        val location = position.location
                        player.sendMessage(
                            StckUtilsPlugin.translationsProvider.translateWithPrefix(
                                "position.show",
                                player.language,
                                "messages",
                                arrayOf(position.name, player.name, location.blockX, location.blockY, location.blockZ)
                            )
                        )
                    }
                }
            }
        }
    }
}
