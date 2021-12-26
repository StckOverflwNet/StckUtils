package de.stckoverflw.stckutils.command

import com.mojang.brigadier.arguments.StringArgumentType
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.extension.hide
import de.stckoverflw.stckutils.extension.isHidden
import de.stckoverflw.stckutils.extension.reveal
import de.stckoverflw.stckutils.util.Permissions
import net.axay.kspigot.commands.*
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Bukkit

class HideCommand {

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
                        ?: return@runs player.sendMessage(StckUtilsPlugin.prefix + "§cno Player with that name found.")
                    val perm = if (player == target) {
                        Permissions.HIDE_SELF
                    } else {
                        Permissions.HIDE_OTHER
                    }
                    if (!player.hasPermission(perm)) {
                        return@runs player.sendMessage(StckUtilsPlugin.prefix + "§cMissing permission: $perm")
                    }
                    if (target.isHidden()) {
                        target.reveal()
                        target.sendMessage(StckUtilsPlugin.prefix + "§ayou were revealed" + if (player != target) " by ${player.name}" else "")
                        player.sendMessage(StckUtilsPlugin.prefix + "§arevealed ${target.name}")
                    } else {
                        target.hide()
                        target.sendMessage(StckUtilsPlugin.prefix + "§ayou were hidden" + if (player != target) " by ${player.name}" else "")
                        player.sendMessage(StckUtilsPlugin.prefix + "§ahid ${target.name}")
                    }
                }
            }
        }
}
