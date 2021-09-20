package de.stckoverflw.stckutils.util

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.minecraft.goal.Goal
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.minecraft.timer.Timer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.items.*
import org.bukkit.Material

/*
 * GUI Items
 */

val placeHolderItemGray = itemStack(Material.GRAY_STAINED_GLASS_PANE) {
    meta {
        name = "§a "
    }
}
val placeHolderItemWhite = itemStack(Material.WHITE_STAINED_GLASS_PANE) {
    meta {
        name = "§a "
    }
}

val goBackItem = itemStack(Material.KNOWLEDGE_BOOK) {
    meta {
        name = "${KColors.LIGHTGREEN}Go back"
        addLore {
            +""
            +"§7§oClick to go back to the previous Page"
        }
    }
}

/*
 * Inventory Items
 */

val settingsItem = itemStack(Material.NETHER_STAR) {
    meta {
        name = "§cSettings"
    }
}

fun generateItemForChallenge(challenge: Challenge) = itemStack(challenge.material) {
    meta {
        name = challenge.name
        localName = challenge.id
        addLore {
            challenge.description.forEach {
                +it
            }
            +" "
            if (challenge.active) {
                +"§aActivated§7, Click to deactivate"
            } else {
                +"§cDeactivated§7, Click to activate"
            }
            if (challenge.configurationGUI() != null) {
                +"§7Right Click to open the Configuration for ${challenge.name}"
            }
        }
    }
}

fun generateItemForGoal(goal: Goal) = itemStack(goal.material) {
    meta {
        name = goal.name
        localName = goal.id
        addLore {
            goal.description.forEach {
                +it
            }
            +" "
            if (GoalManager.activeGoal == goal) {
                +"§aThis Goal is currently activated,"
                +"§7click to §cdeactivate §7it"
            } else {
                +"§cThis Goal is currently deactivated,"
                +"§7click to §aactivate §7it"
            }
        }
    }
}

fun generateTimerItem() = itemStack(Material.CLOCK) {
    meta {
        name = "§6Change Timer Time"
        addLore {
            +" "
            if (Timer.time > 0) {
                +"§7Current Time: $Timer"
            } else {
                +"§7Current Time: §c0m"
            }
            +" "
            +"§7Left-click to higher §c1m"
            +"§7Right-click to lower §c1m"
        }
    }
}

fun generateVillageSpawnItem() = itemStack(Material.VILLAGER_SPAWN_EGG) {
    meta {
        name = "${KColors.SANDYBROWN}Village Spawn"
        addLore {
            +" "
            if (Config.resetSettings.villageSpawn) {
                +"§7Currently §aactivated"
                +" "
                +"§7Click to §cdeactivate"
            } else {
                +"§7Currently §cdeactivated"
                +" "
                +"§7Click to §aactivate"
            }
        }
    }
}
