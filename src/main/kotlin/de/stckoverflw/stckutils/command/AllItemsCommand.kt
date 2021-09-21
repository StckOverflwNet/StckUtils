package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.minecraft.goal.impl.AllItems
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.gui.openGUI

class AllItemsCommand {

    fun register(name: String) = command(name, true) {
        runs {
            if (GoalManager.activeGoal is AllItems) {
                AllItems.resetFilter(player)
                player.openGUI(AllItems.gui())
            } else {
                player.sendMessage("§cAll Items needs to be enabled to do this")
            }
        }
    }
}
