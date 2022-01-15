package de.stckoverflw.stckutils.command

import com.mojang.brigadier.arguments.StringArgumentType
import de.stckoverflw.stckutils.extension.errorTranslatable
import de.stckoverflw.stckutils.extension.hidden
import de.stckoverflw.stckutils.extension.hide
import de.stckoverflw.stckutils.extension.reveal
import de.stckoverflw.stckutils.extension.successTranslatable
import de.stckoverflw.stckutils.util.GUIPage
import de.stckoverflw.stckutils.util.Permissions
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.getArgument
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestListSuspending
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
                        successTranslatable("hide.revealed_by.target", player.name())
                    } else {
                        successTranslatable("hide.revealed.target")
                    }
                )
                player.sendMessage(successTranslatable("hide.revealed.player", target.name()))
            } else {
                target.hide()
                target.sendMessage(
                    if (player != target) {
                        successTranslatable("hide.hidden_by.target", player.name())
                    } else {
                        successTranslatable("hide.hidden.target")
                    }
                )
                player.sendMessage(successTranslatable("hide.hidden.player", target.name()))
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
                            errorTranslatable("general.player_not_found")
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
                player.openGUI(settingsGUI(player.locale()), GUIPage.hidePageNumber)
            }
        }
}
