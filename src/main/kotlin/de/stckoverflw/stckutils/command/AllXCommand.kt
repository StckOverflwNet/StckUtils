package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.extension.errorTranslatable
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
                    player.openGUI(AllItems.gui(player.locale()))
                }
                is AllMobs -> {
                    AllMobs.resetFilter(player)
                    player.openGUI(AllMobs.gui(player.locale()))
                }
                is AllAdvancements -> {
                    AllAdvancements.resetFilter(player)
                    player.openGUI(AllAdvancements.gui(player.locale()))
                }
                else -> player.sendMessage(errorTranslatable("allx.not_enabled"))
            }
        }
    }
}
