package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.Permissions
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.literal
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.openGUI

class TimerCommand {

    fun register() = command("timer", true) {
        requiresPermission(Permissions.TIMER_COMMAND)
        literal("resume") {
            requiresPermission(Permissions.TIMER_RESUME)
            runs {
                Timer.start()
                onlinePlayers.forEach {
                    it.sendMessage(
                        StckUtilsPlugin.translationsProvider.translateWithPrefix(
                            "timer.started",
                            it.language,
                            "messages"
                        )
                    )
                }
            }
        }
        literal("pause") {
            requiresPermission(Permissions.TIMER_PAUSE)
            runs {
                Timer.stop()
                onlinePlayers.forEach {
                    it.sendMessage(
                        StckUtilsPlugin.translationsProvider.translateWithPrefix(
                            "timer.stopped",
                            it.language,
                            "messages"
                        )
                    )
                }
            }
        }
        literal("reset") {
            requiresPermission(Permissions.TIMER_RESET)
            runs {
                Timer.reset()
                onlinePlayers.forEach {
                    it.sendMessage(
                        StckUtilsPlugin.translationsProvider.translateWithPrefix(
                            "timer.reset",
                            it.language,
                            "messages"
                        )
                    )
                }
            }
        }
        runs {
            requiresPermission(Permissions.SETTINGS_GUI)
            player.openGUI(settingsGUI(player.language), -1)
        }
    }
}
