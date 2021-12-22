package de.stckoverflw.stckutils.util

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.minecraft.goal.Goal
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.minecraft.goal.active
import de.stckoverflw.stckutils.minecraft.timer.AccessLevel
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.minecraft.timer.TimerDirection
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
            if (!(challenge.requiresProtocolLib && !StckUtilsPlugin.isProtocolLib)) {
                if (challenge.active) {
                    +"§aActivated§7, Click to deactivate"
                } else {
                    +"§cDeactivated§7, Click to activate"
                }
                if (challenge.configurationGUI() != null) {
                    +"§7Right Click to open the Configuration for ${challenge.name}"
                }
            } else {
                +"§c§lYou need to install ProtocolLib on this server"
                +"§c§lto use this Challenge"
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
            if (GoalManager.activeGoal == goal || goal.active) {
                GoalManager.activeGoal = goal
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
            if (Config.resetSettingsConfig.villageSpawn) {
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

fun generateStartStopTimerItem() = if (Timer.running) {
    itemStack(Material.REDSTONE) {
        meta {
            name = "§6Stop the Timer"
            addLore {
                +" "
                +"§7Click to Stop the Timer"
            }
        }
    }
} else {
    itemStack(Material.EMERALD) {
        meta {
            name = "§aStart the Timer"
            addLore {
                +" "
                +"§7Click to Start the Timer"
            }
        }
    }
}

fun generateJoinRunningItem() = itemStack(Material.ENCHANTED_GOLDEN_APPLE) {
    meta {
        name = "§aJoining while the Timer is running"

        setLore {
            +" "
            when (Timer.joinWhileRunning) {
                AccessLevel.OPERATOR -> {
                    +"§7Currently only §4Operators §7can join"
                }
                AccessLevel.HIDDEN -> {
                    +"§7Currently only §bhidden players §7can join"
                }
                AccessLevel.EVERYONE -> {
                    +"§7Currently §aeveryone §7can join"
                }
                AccessLevel.NONE -> {
                    +"§7Currently §cno one §7can join"
                }
            }
            +"§7while the Timer is running."
        }
    }
}

fun generateTimerDirectionItem() = itemStack(Material.REPEATER) {
    meta {
        name = "§aChange the direction of the Timer"

        setLore {
            +" "
            when (Timer.direction) {
                TimerDirection.FORWARDS -> {
                    +"§7The Timer is currently running §aforwards§7."
                }
                TimerDirection.BACKWARDS -> {
                    +"§7The Timer is currently running §dbackwards§7."
                }
            }
        }
    }
}
