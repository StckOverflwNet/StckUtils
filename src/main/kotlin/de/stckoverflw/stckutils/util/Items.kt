package de.stckoverflw.stckutils.util

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.hidden
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
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

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

const val scrollRightb64 =
    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19"
const val scrollLeftb64 =
    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ=="
const val scrollUpb64 =
    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA0MGZlODM2YTZjMmZiZDJjN2E5YzhlYzZiZTUxNzRmZGRmMWFjMjBmNTVlMzY2MTU2ZmE1ZjcxMmUxMCJ9fX0="
const val scrollDownb64 =
    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQzNzM0NmQ4YmRhNzhkNTI1ZDE5ZjU0MGE5NWU0ZTc5ZGFlZGE3OTVjYmM1YTEzMjU2MjM2MzEyY2YifX19"

val scrollRightItem = getTextureHead(scrollRightb64, "§fScroll Right")

val scrollLeftItem = getTextureHead(scrollLeftb64, "§fScroll Left")

val scrollUpItem = getTextureHead(scrollUpb64, "§fScroll Up")

val scrollDownItem = getTextureHead(scrollDownb64, "§fScroll Down")

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

fun generateItemForJoinWhileRunning(accessLevel: AccessLevel): ItemStack {
    val item = itemStack(
        if (Timer.joinWhileRunning.contains(accessLevel)) {
            Material.GREEN_WOOL
        } else {
            Material.RED_WOOL
        }
    ) {
        meta {
            name = accessLevel.name.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            setLore {
                when (accessLevel) {
                    AccessLevel.OPERATOR -> {
                        +"§4Operators §7can join"
                    }
                    AccessLevel.HIDDEN -> {
                        +"§bHidden players §7can join"
                    }
                    AccessLevel.EVERYONE -> {
                        +"§aEveryone §7can join"
                    }
                    AccessLevel.NONE -> {
                        +"§cNo one §7can join"
                    }
                }
                +"§7while the Timer is running."
                +"§7Click to ${if (Timer.joinWhileRunning.contains(accessLevel)) "§cdeactivate" else "§aactivate"}"
            }
        }
    }
    return item
}

fun generateItemForHide(player: Player): ItemStack {
    val head = getPlayerHead(player)
    val meta = head.itemMeta
    meta.setLore {
        +" "
        if (player.hidden) {
            +"§7Currently ${player.name} is §ahidden"
            +"§7Click to §areveal ${player.name}"
        } else {
            +"§7Currently ${player.name} is §arevealed"
            +"§7Click to §ahide ${player.name}"
        }
    }
    if (player.hidden) {
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
    }
    head.itemMeta = meta
    return head
}

fun getPlayerHead(player: OfflinePlayer): ItemStack {
    val head = itemStack(Material.PLAYER_HEAD) {
        meta {
            name = player.name
        }
    }
    val meta = head.itemMeta
    (meta as SkullMeta).owningPlayer = player
    head.itemMeta = meta
    return head
}

fun getTextureHead(b64: String, name: String): ItemStack {
    val head = itemStack(Material.PLAYER_HEAD) {
        meta {
            this.name = name
        }
    }
    val meta = head.itemMeta
    try {
        val profileField = meta.javaClass.getDeclaredField("profile")
        profileField.isAccessible = true
        profileField.set(meta, textureProfile(b64))
    } catch (exception: NoSuchFieldException) {
        exception.printStackTrace()
    } catch (exception: IllegalArgumentException) {
        exception.printStackTrace()
    } catch (exception: IllegalAccessException) {
        exception.printStackTrace()
    }
    head.itemMeta = meta
    return head
}

private fun textureProfile(b64: String): GameProfile {
    val id = UUID(
        b64.substring(b64.length - 20).hashCode().toLong(),
        b64.substring(b64.length - 10).hashCode().toLong()
    )
    val profile = GameProfile(id, "Player")
    profile.properties.put("textures", Property("textures", b64))
    return profile
}
