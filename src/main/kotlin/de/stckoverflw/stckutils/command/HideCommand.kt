package de.stckoverflw.stckutils.command

import com.mojang.brigadier.arguments.StringArgumentType
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.extension.hidden
import de.stckoverflw.stckutils.extension.hide
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.extension.reveal
import de.stckoverflw.stckutils.util.GUIPage
import de.stckoverflw.stckutils.util.Permissions
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.commands.*
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.openGUI
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class HideCommand {

    companion object {
        fun sendResponse(player: Player, target: Player) {
            if (target.hidden) {
                target.reveal()
                target.sendMessage(
                    if (player != target) {
                        StckUtilsPlugin.translationsProvider.translateWithPrefix(
                            "hide.revealed_by.target",
                            target.language,
                            "messages",
                            arrayOf(player.name)
                        )
                    } else {
                        StckUtilsPlugin.translationsProvider.translateWithPrefix(
                            "hide.revealed.target",
                            target.language,
                            "messages"
                        )
                    }
                )
                player.sendMessage(
                    StckUtilsPlugin.translationsProvider.translateWithPrefix(
                        "hide.revealed.player",
                        player.language,
                        "messages",
                        arrayOf(target.name)
                    )
                )
            } else {
                target.hide()
                target.sendMessage(
                    if (player != target) {
                        StckUtilsPlugin.translationsProvider.translateWithPrefix(
                            "hide.hidden_by.target",
                            target.language,
                            "messages",
                            arrayOf(player.name)
                        )
                    } else {
                        StckUtilsPlugin.translationsProvider.translateWithPrefix(
                            "hide.hidden.target",
                            target.language,
                            "messages"
                        )
                    }
                )
                player.sendMessage(
                    StckUtilsPlugin.translationsProvider.translateWithPrefix(
                        "hide.hidden.player",
                        player.language,
                        "messages",
                        arrayOf(target.name)
                    )
                )
            }
        }
    }

    fun register() =
        command("hide", true) {
            requiresPermission(Permissions.HIDE_COMMAND)
            argument("player", StringArgumentType.string()) {
                suggestListSuspending { suggest ->
                    onlinePlayers.map { it.name }.filter {
                        if (suggest.input != null && suggest.input.substring(suggest.input.length - 1) != " ")
                            it.startsWith(
                                suggest.getArgument<String>("player"),
                                true
                            ) else
                            true
                    }.sorted()
                }
                runs runs@{
                    val target = Bukkit.getPlayer(getArgument<String>("player"))
                        ?: return@runs player.sendMessage(
                            StckUtilsPlugin.translationsProvider.translateWithPrefix(
                                "general.player_not_found",
                                player.language,
                                "messages"
                            )
                        )
                    val perm = if (player == target) {
                        Permissions.HIDE_SELF
                    } else {
                        Permissions.HIDE_OTHER
                    }
                    requiresPermission(perm)
                    sendResponse(player, target)
                }
            }
            runs {
                player.openGUI(settingsGUI(player.language), GUIPage.hidePageNumber)
            }
        }
}
