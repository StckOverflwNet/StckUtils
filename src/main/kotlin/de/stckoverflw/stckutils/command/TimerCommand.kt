package de.stckoverflw.stckutils.command

import com.mojang.brigadier.arguments.StringArgumentType
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.commands.*
import net.axay.kspigot.gui.openGUI
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class TimerCommand {

    fun register() = command("timer", true) {
        requiresPermission("stckutils.command.timer")
        argument("action", StringArgumentType.string()) {
            suggestListSuspending { suggest ->
                listOf("resume", "pause", "reset").filter {
                    if (suggest.input != null && suggest.input.substring(suggest.input.length - 1) != " ")
                        it.startsWith(
                            suggest.getArgument<String>("action"),
                            true
                        ) else
                        true
                }.sorted()
            }
            runs {
                when (getArgument<String>("action").lowercase()) {
                    "resume" -> {
                        Timer.start()
                        Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §astarted"))
                    }
                    "pause" -> {
                        Timer.stop()
                        Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §6stopped"))
                    }
                    "reset" -> {
                        Timer.reset()
                        Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §cresetted"))
                    }
                }
            }
        }
        runs {
            player.openGUI(settingsGUI(), -1)
        }
    }
}
