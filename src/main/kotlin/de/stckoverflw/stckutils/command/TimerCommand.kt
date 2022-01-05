package de.stckoverflw.stckutils.command

import com.mojang.brigadier.arguments.StringArgumentType
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.Permissions
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.commands.*
import net.axay.kspigot.gui.openGUI
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class TimerCommand {

    fun register() = command("timer", true) {
        requiresPermission(Permissions.TIMER_COMMAND)
        literal("resume") {
            runs {
                if (!player.hasPermission(Permissions.TIMER_RESUME)) {
                    return@runs player.sendMessage(StckUtilsPlugin.prefix + "§cMissing permission: ${Permissions.TIMER_RESUME}")
                }
                Timer.start()
                Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §astarted"))
            }
        }
        literal("pause") {
            runs {
                if (!player.hasPermission(Permissions.TIMER_PAUSE)) {
                    return@runs player.sendMessage(StckUtilsPlugin.prefix + "§cMissing permission: ${Permissions.TIMER_PAUSE}")
                }
                Timer.stop()
                Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §6stopped"))
            }
        }
        literal("reset") {
            runs {
                if (!player.hasPermission(Permissions.TIMER_RESET)) {
                    return@runs player.sendMessage(StckUtilsPlugin.prefix + "§cMissing permission: ${Permissions.TIMER_RESET}")
                }
                Timer.reset()
                Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §cresetted"))
            }
        }
        runs {
            if (!player.hasPermission(Permissions.SETTINGS_GUI)) {
                return@runs player.sendMessage(StckUtilsPlugin.prefix + "§cMissing permission: ${Permissions.SETTINGS_GUI}")
            }
            player.openGUI(settingsGUI(), -1)
        }
    }
}
