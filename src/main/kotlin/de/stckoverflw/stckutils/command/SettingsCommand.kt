package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.user.settingsGUI
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.gui.openGUI

class SettingsCommand {

    fun register() = command("settings", true) {
        requiresPermission("stckutils.command.settings")
        runs {
            player.openGUI(settingsGUI())
        }
    }
}
