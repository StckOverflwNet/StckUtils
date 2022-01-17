package de.stckoverflw.stckutils.command

import com.mojang.brigadier.arguments.StringArgumentType
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.config.data.PositionData
import de.stckoverflw.stckutils.extension.sendPrefixMessage
import de.stckoverflw.stckutils.extension.successTranslatable
import de.stckoverflw.stckutils.util.Permissions
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.getArgument
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestListSuspending
import net.axay.kspigot.extensions.onlinePlayers
import net.kyori.adventure.text.Component.text

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
                        it.sendPrefixMessage(
                            successTranslatable(
                                "position.create",
                                player.name(),
                                text(name),
                                text(location.blockX),
                                text(location.blockY),
                                text(location.blockZ)
                            )
                        )
                    }
                } else {
                    requiresPermission(Permissions.POSITION_SHOW)
                    val position = Config.positionDataConfig.positions.find {
                        it.name == getArgument<String>("name")
                    } ?: return@runs
                    val location = position.location
                    player.sendPrefixMessage(
                        successTranslatable(
                            "position.show",
                            text(position.name),
                            player.name(),
                            text(location.blockX),
                            text(location.blockY),
                            text(location.blockZ)
                        )
                    )
                }
            }
        }
    }
}
