package de.stckoverflw.stckutils.util

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.hidden
import de.stckoverflw.stckutils.minecraft.challenge.*
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

fun getScrollRightItem(locale: Locale) = getTextureHead(
    scrollRightb64,
    StckUtilsPlugin.translationsProvider.translate(
        "gui.scroll_right",
        locale,
        "items"
    )
)

fun getScrollLeftItem(locale: Locale) = getTextureHead(
    scrollLeftb64,
    StckUtilsPlugin.translationsProvider.translate(
        "gui.scroll_left",
        locale,
        "items"
    )
)

fun getScrollUpItem(locale: Locale) = getTextureHead(
    scrollUpb64,
    StckUtilsPlugin.translationsProvider.translate(
        "gui.scroll_up",
        locale,
        "items"
    )
)

fun getScrollDownItem(locale: Locale) = getTextureHead(
    scrollDownb64,
    StckUtilsPlugin.translationsProvider.translate(
        "gui.scroll_down",
        locale,
        "items"
    )
)

fun getGoBackItem(locale: Locale) = itemStack(Material.KNOWLEDGE_BOOK) {
    meta {
        name = StckUtilsPlugin.translationsProvider.translate(
            "gui.back.name",
            locale,
            "items"
        )
        addLore {
            +""
            StckUtilsPlugin.translationsProvider.translate(
                "gui.back.lore",
                locale,
                "items"
            ).split("\n").forEach {
                +it
            }
        }
    }
}

/*
 * Inventory Items
 */

fun getSettingsItem(locale: Locale) = itemStack(Material.NETHER_STAR) {
    meta {
        name = StckUtilsPlugin.translationsProvider.translate(
            "settings.name",
            locale,
            "items"
        )
    }
}

fun generateItemForChallenge(challenge: Challenge, locale: Locale) = itemStack(challenge.material) {
    meta {
        name = ChallengeManager.translationsProvider.translate(
            nameKey,
            locale,
            challenge.id
        )
        localName = challenge.id
        addLore {
            ChallengeManager.translationsProvider.translate(
                descriptionKey,
                locale,
                challenge.id
            ).split("\n").forEach {
                +it
            }
            +" "
            if (!(challenge.requiresProtocolLib && !StckUtilsPlugin.isProtocolLib)) {
                if (challenge.active) {
                    StckUtilsPlugin.translationsProvider.translate(
                        "gui.challenge.lore.active",
                        locale,
                        "items"
                    ).split("\n").forEach {
                        +it
                    }
                } else {
                    StckUtilsPlugin.translationsProvider.translate(
                        "gui.challenge.lore.inactive",
                        locale,
                        "items"
                    ).split("\n").forEach {
                        +it
                    }
                }
                if (challenge.configurationGUI(locale) != null) {
                    StckUtilsPlugin.translationsProvider.translate(
                        "gui.challenge.lore.config_gui",
                        locale,
                        "items",
                        arrayOf(
                            ChallengeManager.translationsProvider.translate(
                                nameKey,
                                locale,
                                challenge.id
                            )
                        )
                    ).split("\n").forEach {
                        +it
                    }
                }
            } else {
                StckUtilsPlugin.translationsProvider.translate(
                    "gui.challenge.lore.protocol_lib",
                    locale,
                    "items"
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }
}

fun generateItemForGoal(goal: Goal, locale: Locale) = itemStack(goal.material) {
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
                StckUtilsPlugin.translationsProvider.translate(
                    "gui.goal.lore.active",
                    locale,
                    "items"
                ).split("\n").forEach {
                    +it
                }
            } else {
                StckUtilsPlugin.translationsProvider.translate(
                    "gui.goal.lore.inactive",
                    locale,
                    "items"
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }
}

fun generateTimerItem(locale: Locale) = itemStack(Material.CLOCK) {
    meta {
        name = StckUtilsPlugin.translationsProvider.translate(
            "gui.timer.name",
            locale,
            "items"
        )
        addLore {
            +" "
            StckUtilsPlugin.translationsProvider.translate(
                "gui.timer.lore",
                locale,
                "items",
                arrayOf(Timer)
            ).split("\n").forEach {
                +it
            }
        }
    }
}

fun generateVillageSpawnItem(locale: Locale) = itemStack(Material.VILLAGER_SPAWN_EGG) {
    meta {
        name = KColors.SANDYBROWN.toString().plus(
            StckUtilsPlugin.translationsProvider.translate(
                "gui.village_spawn.name",
                locale,
                "items"
            )
        )
        addLore {
            +" "
            if (Config.resetSettingsConfig.villageSpawn) {
                StckUtilsPlugin.translationsProvider.translate(
                    "gui.village_spawn.lore.activated",
                    locale,
                    "items"
                ).split("\n").forEach {
                    +it
                }
            } else {
                StckUtilsPlugin.translationsProvider.translate(
                    "gui.village_spawn.lore.deactivated",
                    locale,
                    "items"
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }
}

fun generateStartStopTimerItem(locale: Locale) = if (Timer.running) {
    itemStack(Material.REDSTONE) {
        meta {
            name = StckUtilsPlugin.translationsProvider.translate(
                "gui.start_stop_timer.running.name",
                locale,
                "items"
            )
            addLore {
                +" "
                StckUtilsPlugin.translationsProvider.translate(
                    "gui.start_stop_timer.running.lore",
                    locale,
                    "items"
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }
} else {
    itemStack(Material.EMERALD) {
        meta {
            name = StckUtilsPlugin.translationsProvider.translate(
                "gui.start_stop_timer.stopped.name",
                locale,
                "items"
            )
            addLore {
                +" "
                StckUtilsPlugin.translationsProvider.translate(
                    "gui.start_stop_timer.stopped.lore",
                    locale,
                    "items"
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }
}

fun generateTimerDirectionItem(locale: Locale) = itemStack(Material.REPEATER) {
    meta {
        name = StckUtilsPlugin.translationsProvider.translate(
            "gui.timer_direction.name",
            locale,
            "items"
        )

        setLore {
            +" "
            when (Timer.direction) {
                TimerDirection.FORWARDS -> {
                    StckUtilsPlugin.translationsProvider.translate(
                        "gui.timer_direction.lore.forwards",
                        locale,
                        "items"
                    ).split("\n").forEach {
                        +it
                    }
                }
                TimerDirection.BACKWARDS -> {
                    StckUtilsPlugin.translationsProvider.translate(
                        "gui.timer_direction.lore.backwards",
                        locale,
                        "items"
                    ).split("\n").forEach {
                        +it
                    }
                }
            }
        }
    }
}

fun generateItemForJoinWhileRunning(accessLevel: AccessLevel, locale: Locale): ItemStack {
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
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.join_while_running.lore.operator",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                    AccessLevel.HIDDEN -> {
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.join_while_running.lore.hidden",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                    AccessLevel.EVERYONE -> {
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.join_while_running.lore.everyone",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                    AccessLevel.NONE -> {
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.join_while_running.lore.none",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
                StckUtilsPlugin.translationsProvider.translate(
                    "gui.join_while_running.lore",
                    locale,
                    "items",
                    arrayOf(
                        if (Timer.joinWhileRunning.contains(accessLevel)) {
                            "§c".plus(
                                StckUtilsPlugin.translationsProvider.translate(
                                    "generic.disable",
                                    locale,
                                    "general"
                                )
                            )
                        } else {
                            "§a".plus(
                                StckUtilsPlugin.translationsProvider.translate(
                                    "generic.activate",
                                    locale,
                                    "general"
                                )
                            )
                        }
                    )
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }
    return item
}

fun generateItemForHide(player: Player, locale: Locale): ItemStack {
    val head = getPlayerHead(player)
    val meta = head.itemMeta
    meta.setLore {
        +" "
        if (player.hidden) {
            StckUtilsPlugin.translationsProvider.translate(
                "gui.hide.hidden.lore",
                locale,
                "items",
                arrayOf(player.name)
            ).split("\n").forEach {
                +it
            }
        } else {
            StckUtilsPlugin.translationsProvider.translate(
                "gui.hide.revealed.lore",
                locale,
                "items",
                arrayOf(player.name)
            ).split("\n").forEach {
                +it
            }
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
