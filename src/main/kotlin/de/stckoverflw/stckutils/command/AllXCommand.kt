package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.minecraft.goal.impl.AllAdvancements
import de.stckoverflw.stckutils.minecraft.goal.impl.AllItems
import de.stckoverflw.stckutils.minecraft.goal.impl.AllMobs
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.gui.openGUI

class AllXCommand {

    fun register() = command("allx", true) {
        runs {
            when (GoalManager.activeGoal) {
                is AllItems -> {
                    AllItems.resetFilter(player)
                    player.openGUI(AllItems.gui(player.language))
                }
                is AllMobs -> {
                    AllMobs.resetFilter(player)
                    player.openGUI(AllMobs.gui(player.language))
                }
                is AllAdvancements -> {
                    AllAdvancements.resetFilter(player)
                    player.openGUI(AllAdvancements.gui(player.language))
                }
                else -> player.sendMessage(
                    StckUtilsPlugin.translationsProvider.translateWithPrefix(
                        "allx.not_enabled",
                        player.language,
                        "messages"
                    )
                )
            }
        }
    }
}
