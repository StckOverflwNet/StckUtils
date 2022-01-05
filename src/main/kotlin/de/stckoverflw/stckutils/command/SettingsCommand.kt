package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.util.Permissions
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.gui.openGUI

class SettingsCommand {

    fun register() = command("settings", true) {
        requiresPermission(Permissions.SETTINGS_COMMAND)
        runs {
            if (!player.hasPermission(Permissions.SETTINGS_GUI)) {
                return@runs player.sendMessage(StckUtilsPlugin.prefix + "§cMissing permission: ${Permissions.SETTINGS_GUI}")
            }
            player.openGUI(settingsGUI(player.language))
        }
    }
}
