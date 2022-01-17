package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.extension.sendPrefixMessage
import de.stckoverflw.stckutils.extension.successTranslatable
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.GUIPage
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
        literal("start") {
            requiresPermission(Permissions.TIMER_START)
            runs {
                Timer.start()
                onlinePlayers.forEach {
                    it.sendPrefixMessage(successTranslatable("timer.started"))
                }
            }
        }
        literal("pause") {
            requiresPermission(Permissions.TIMER_PAUSE)
            runs {
                Timer.stop()
                onlinePlayers.forEach {
                    it.sendPrefixMessage(successTranslatable("timer.stopped"))
                }
            }
        }
        literal("reset") {
            requiresPermission(Permissions.TIMER_RESET)
            runs {
                Timer.reset()
                onlinePlayers.forEach {
                    it.sendPrefixMessage(successTranslatable("timer.reset"))
                }
            }
        }
        runs {
            requiresPermission(Permissions.SETTINGS_GUI)
            player.openGUI(settingsGUI(player.locale()), GUIPage.timerPageNumber)
        }
    }
}
