package de.stckoverflw.stckutils.util

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.Colors
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.hidden
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.minecraft.challenge.descriptionKey
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.minecraft.goal.Goal
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.minecraft.goal.active
import de.stckoverflw.stckutils.minecraft.goal.descriptionKey
import de.stckoverflw.stckutils.minecraft.goal.nameKey
import de.stckoverflw.stckutils.minecraft.timer.AccessLevel
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.minecraft.timer.TimerDirection
import net.axay.kspigot.extensions.bukkit.javaAwtColor
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.flags
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.items.setLore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.space
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.Locale
import java.util.UUID

/* Textures */

object Textures {

    const val SCROLL_RIGHT_B64 =
        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19"
    const val SCROLL_LEFT_B64 =
        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ=="
    const val SCROLL_UP_B64 =
        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA0MGZlODM2YTZjMmZiZDJjN2E5YzhlYzZiZTUxNzRmZGRmMWFjMjBmNTVlMzY2MTU2ZmE1ZjcxMmUxMCJ9fX0="
    const val SCROLL_DOWN_B64 =
        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQzNzM0NmQ4YmRhNzhkNTI1ZDE5ZjU0MGE5NWU0ZTc5ZGFlZGE3OTVjYmM1YTEzMjU2MjM2MzEyY2YifX19"
}

/* GUI Items */

val placeHolderItemGray =
    itemStack(Material.GRAY_STAINED_GLASS_PANE) {
        meta {
            name = space()
        }
    }

val placeHolderItemWhite =
    itemStack(Material.WHITE_STAINED_GLASS_PANE) {
        meta {
            name = space()
        }
    }

fun getScrollRightItem(locale: Locale) =
    getTextureHead(
        Textures.SCROLL_RIGHT_B64,
        translatable("gui.scroll_right", Colors.ACCENT_AQUA).render(locale)
    )

fun getScrollLeftItem(locale: Locale) =
    getTextureHead(
        Textures.SCROLL_LEFT_B64,
        translatable("gui.scroll_left", Colors.ACCENT_AQUA).render(locale)
    )

fun getScrollUpItem(locale: Locale) =
    getTextureHead(
        Textures.SCROLL_UP_B64,
        translatable("gui.scroll_up", Colors.ACCENT_AQUA).render(locale)
    )

fun getScrollDownItem(locale: Locale) =
    getTextureHead(
        Textures.SCROLL_DOWN_B64,
        translatable("gui.scroll_down", Colors.ACCENT_AQUA).render(locale)
    )

fun getGoBackItem(locale: Locale) =
    itemStack(Material.KNOWLEDGE_BOOK) {
        meta {
            name = translatable("gui.back.name")
                .color(Colors.GO_BACK)
                .render(locale)

            addLore {
                +""
                +translatable("gui.back.lore")
                    .color(Colors.GO_BACK_SECONDARY)
                    .render(locale)
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
            )
        }
    }

fun getSettingsItem(locale: Locale) =
    itemStack(Material.NETHER_STAR) {
        meta {
            name = translatable("settings.name")
                .color(Colors.SETTINGS)
                .render(locale)

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
            )
        }
    }

fun generateChallengeGuiItem(locale: Locale) =
    itemStack(Material.DRAGON_HEAD) {
        meta {
            name = translatable("gui.page_changer.challenges.name")
                .color(Colors.CHALLENGE)
                .render(locale)

            addLore {
                addComponent(
                    translatable("gui.page_changer.challenges.lore")
                        .color(Colors.CHALLENGE_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateGameChangeGuiItem(locale: Locale) =
    itemStack(Material.MAP) {
        meta {
            name = translatable("gui.page_changer.game_change.name")
                .color(Colors.GAME_CHANGE)
                .render(locale)

            addLore {
                addComponent(
                    translatable("gui.page_changer.game_change.lore")
                        .color(Colors.GAME_CHANGE_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateGoalGuiItem(locale: Locale) =
    itemStack(Material.DIAMOND) {
        meta {
            name = translatable("gui.page_changer.goals.name")
                .color(Colors.GOAL)
                .render(locale)

            addLore {
                addComponent(
                    translatable("gui.page_changer.goals.lore")
                        .color(Colors.GOAL_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateMoreSettingsGuiItem(locale: Locale) =
    itemStack(Material.COMPARATOR) {
        meta {
            name = translatable("gui.page_changer.more_settings.name")
                .color(Colors.MORE_SETTINGS)
                .render(locale)

            addLore {
                addComponent(
                    translatable("gui.page_changer.more_settings.lore")
                        .color(Colors.MORE_SETTINGS_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateWorldResetGuiItem(locale: Locale) =
    itemStack(Material.GRASS_BLOCK) {
        meta {
            name = translatable("gui.page_changer.world_reset.name")
                .color(Colors.WORLD_RESET_GUI)
                .render(locale)

            addLore {
                addComponent(
                    translatable("gui.page_changer.world_reset.lore")
                        .color(Colors.WORLD_RESET_GUI_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateTimerSettingsGuiItem(locale: Locale) =
    itemStack(Material.CLOCK) {
        meta {
            name = translatable("gui.page_changer.timer.name")
                .color(Colors.TIMER_SETTINGS)
                .render(locale)

            addLore {
                addComponent(
                    translatable("gui.page_changer.timer.lore")
                        .color(Colors.TIMER_SETTINGS_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateTeamGoalItem(locale: Locale) =
    itemStack(Material.AZALEA) {
        meta {
            name = translatable("gui.team_goal.name")
                .color(Colors.GOAL_TYPE)
                .render(locale)

            addLore {
                +space()
                addComponent(
                    translatable("gui.team_goal.lore")
                        .color(Colors.GOAL_TYPE_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateBattleGoalItem(locale: Locale) =
    itemStack(Material.NETHERITE_SWORD) {
        meta {
            name = translatable("gui.battle.name")
                .color(Colors.GOAL_TYPE)
                .render(locale)

            addLore {
                +space()
                addComponent(
                    translatable("gui.battle.lore")
                        .color(Colors.GOAL_TYPE_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateJoinWhileRunningGuiItem(locale: Locale) =
    itemStack(Material.ENCHANTED_GOLDEN_APPLE) {
        meta {
            name = translatable("gui.page_changer.join_while_running.name")
                .color(Colors.JOIN_WHILE_RUNNING)
                .render(locale)

            setLore {
                +space()
                addComponent(
                    translatable("gui.page_changer.join_while_running.lore")
                        .color(Colors.JOIN_WHILE_RUNNING_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateResetTimerGuiItem(locale: Locale) =
    itemStack(Material.BARRIER) {
        meta {
            name = translatable("gui.reset_timer.name")
                .color(Colors.RESET)
                .render(locale)

            addLore {
                +space()
                addComponent(
                    translatable("gui.reset_timer.lore")
                        .color(Colors.RESET_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateTimerColorGuiItem(locale: Locale) =
    itemStack(Material.ORANGE_DYE) {
        meta {
            name = translatable("gui.page_changer.color.name")
                .color(Colors.TIMER_COLOR)
                .render(locale)

            addLore {
                +space()
                addComponent(
                    translatable("gui.page_changer.color.lore")
                        .color(Colors.TIMER_COLOR_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateColorCompoundItem(chatColor: ChatColor, locale: Locale) =
    itemStack(
        when (chatColor) {
            ChatColor.DARK_RED -> Material.RED_DYE
            ChatColor.RED -> Material.RED_DYE
            ChatColor.GOLD -> Material.ORANGE_DYE
            ChatColor.YELLOW -> Material.YELLOW_DYE
            ChatColor.DARK_GREEN -> Material.GREEN_DYE
            ChatColor.GREEN -> Material.GREEN_DYE
            ChatColor.AQUA -> Material.CYAN_DYE
            ChatColor.DARK_AQUA -> Material.CYAN_DYE
            ChatColor.DARK_BLUE -> Material.BLUE_DYE
            ChatColor.BLUE -> Material.LIGHT_BLUE_DYE
            ChatColor.LIGHT_PURPLE -> Material.PURPLE_DYE
            ChatColor.DARK_PURPLE -> Material.PURPLE_DYE
            ChatColor.WHITE -> Material.WHITE_DYE
            ChatColor.GRAY -> Material.LIGHT_GRAY_DYE
            ChatColor.DARK_GRAY -> Material.GRAY_DYE
            else -> Material.BLACK_DYE
        }
    ) {
        meta {
            name = text(chatColor.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) })
                .color(TextColor.color(chatColor.javaAwtColor.rgb))

            addLore {
                +space()
                addComponent(
                    translatable(
                        "gui.color_compound.lore",
                        listOf(
                            text(
                                chatColor.toString().plus(
                                    chatColor.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }
                                )
                            ),
                            text(
                                net.md_5.bungee.api.ChatColor.stripColor(Timer.formatTime(90061.toLong())) ?: "n/a", // 1d 1h 1m 1s
                                TextColor.color(chatColor.asBungee().color.rgb)
                            )
                        )
                    )
                        .color(Colors.COLOR_COMPOUND_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateWorldResetItem(locale: Locale) =
    itemStack(Material.BARRIER) {
        meta {
            name = translatable("gui.world_reset.name")
                .color(Colors.WORLD_RESET)
                .render(locale)

            addLore {
                +space()
                addComponent(
                    translatable("gui.world_reset.lore")
                        .color(Colors.WORLD_RESET_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateItemForChallenge(challenge: Challenge, locale: Locale) =
    itemStack(challenge.material) {
        meta {
            name = translatable(challenge.nameKey)
                .color(Colors.CHALLENGE_COMPOUND)
                .render(locale)

            addLore {
                addComponent(
                    translatable(challenge.descriptionKey)
                        .color(Colors.CHALLENGE_COMPOUND_SECONDARY)
                        .render(locale)
                )
                +space()
                if (!(challenge.requiresProtocolLib && !StckUtilsPlugin.isProtocolLib)) {
                    if (challenge.active) {
                        addComponent(
                            translatable("gui.challenge.lore.active")
                                .color(Colors.ACTIVE)
                                .render(locale)
                        )
                    } else {
                        addComponent(
                            translatable("gui.challenge.lore.inactive")
                                .color(Colors.INACTIVE)
                                .render(locale)
                        )
                    }
                    if (challenge.configurationGUI(Config.languageConfig.defaultLanguage) != null) {
                        addComponent(
                            translatable(
                                "gui.challenge.lore.config_gui",
                                listOf(
                                    translatable(challenge.nameKey)
                                        .color(Colors.CONFIGURATION)
                                )
                            )
                                .color(Colors.CONFIGURATION)
                                .render(locale)
                        )
                    }
                } else {
                    addComponent(
                        translatable("gui.challenge.lore.protocol_lib")
                            .color(Colors.ERROR)
                            .render(locale)
                            .decorate(TextDecoration.BOLD)
                    )
                }
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
            )
        }
    }

fun generateItemForGoal(goal: Goal, locale: Locale) =
    itemStack(goal.material) {
        meta {
            name = translatable(goal.nameKey)
                .color(Colors.GOAL_COMPOUND)
                .render(locale)

            addLore {
                addComponent(
                    translatable(goal.descriptionKey)
                        .color(Colors.GOAL_COMPOUND_SECONDARY)
                        .render(locale)
                )
                +space()
                if (GoalManager.activeGoal == goal || goal.active) {
                    GoalManager.activeGoal = goal
                    addComponent(
                        translatable("gui.goal.lore.active")
                            .color(Colors.ACTIVE)
                            .render(locale)
                    )
                } else {
                    addComponent(
                        translatable("gui.goal.lore.inactive")
                            .color(Colors.INACTIVE)
                            .render(locale)
                    )
                }
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
        }
    }

fun generateTimerItem(locale: Locale) =
    itemStack(Material.CLOCK) {
        meta {
            name = translatable("gui.timer.name")
                .color(Colors.TIMER_ITEM)
                .render(locale)

            addLore {
                +space()
                addComponent(
                    translatable("gui.timer.lore", listOf(text(Timer.toString()).color(Timer.color)))
                        .color(Colors.TIMER_ITEM_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
            )
        }
    }

fun generateVillageSpawnItem(locale: Locale) =
    itemStack(Material.VILLAGER_SPAWN_EGG) {
        meta {
            name = translatable("gui.village_spawn.name")
                .color(Colors.VILLAGE_SPAWN)
                .render(locale)

            addLore {
                +space()
                if (Config.resetSettingsConfig.villageSpawn) {
                    addComponent(
                        translatable("gui.village_spawn.lore.activated")
                            .color(Colors.VILLAGE_SPAWN_SECONDARY)
                            .render(locale)
                    )
                } else {
                    addComponent(
                        translatable("gui.village_spawn.lore.deactivated")
                            .color(Colors.VILLAGE_SPAWN_SECONDARY)
                            .render(locale)
                    )
                }
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
            )
        }
    }

fun generateStartStopTimerItem(locale: Locale) =
    if (Timer.running) {
        itemStack(Material.REDSTONE) {
            meta {
                name = translatable("gui.start_stop_timer.running.name")
                    .color(Colors.INACTIVE)
                    .render(locale)

                addLore {
                    +space()
                    addComponent(
                        translatable("gui.start_stop_timer.running.lore")
                            .color(Colors.SECONDARY)
                            .render(locale)
                    )
                }

                flags(
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_DESTROYS,
                    ItemFlag.HIDE_DYE,
                    ItemFlag.HIDE_PLACED_ON,
                    ItemFlag.HIDE_POTION_EFFECTS,
                    ItemFlag.HIDE_UNBREAKABLE
                )
            }
        }
    } else {
        itemStack(Material.EMERALD) {
            meta {
                name = translatable("gui.start_stop_timer.stopped.name")
                    .color(Colors.ACTIVE)
                    .render(locale)

                addLore {
                    +space()
                    addComponent(
                        translatable("gui.start_stop_timer.stopped.lore")
                            .color(Colors.SECONDARY)
                            .render(locale)
                    )
                }

                flags(
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_DESTROYS,
                    ItemFlag.HIDE_DYE,
                    ItemFlag.HIDE_PLACED_ON,
                    ItemFlag.HIDE_POTION_EFFECTS,
                    ItemFlag.HIDE_UNBREAKABLE
                )
            }
        }
    }

fun generateTimerDirectionItem(locale: Locale) =
    itemStack(Material.REPEATER) {
        meta {
            name = translatable("gui.timer_direction.name")
                .color(Colors.TIMER_DIRECTION)
                .render(locale)

            setLore {
                +space()
                when (Timer.direction) {
                    TimerDirection.FORWARDS -> {
                        addComponent(
                            translatable("gui.timer_direction.lore.forwards")
                                .color(Colors.ACTIVE)
                                .render(locale)
                        )
                    }
                    TimerDirection.BACKWARDS -> {
                        addComponent(
                            translatable("gui.timer_direction.lore.backwards")
                                .color(Colors.INACTIVE)
                                .render(locale)
                        )
                    }
                }
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
            )
        }
    }

fun generateItemForJoinWhileRunning(accessLevel: AccessLevel, locale: Locale) =
    itemStack(
        if (Timer.joinWhileRunning.contains(accessLevel)) {
            Material.GREEN_WOOL
        } else {
            Material.RED_WOOL
        }
    ) {
        meta {
            name = text(
                accessLevel.name.replace("_", " ")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            )
                .color(Colors.JOIN_WHILE_RUNNING)

            setLore {
                when (accessLevel) {
                    AccessLevel.OPERATOR -> {
                        addComponent(
                            translatable("gui.join_while_running.lore.operator")
                                .color(Colors.JOIN_WHILE_RUNNING_SECONDARY)
                                .render(locale)
                        )
                    }
                    AccessLevel.HIDDEN -> {
                        addComponent(
                            translatable("gui.join_while_running.lore.hidden")
                                .color(Colors.JOIN_WHILE_RUNNING_SECONDARY)
                                .render(locale)
                        )
                    }
                    AccessLevel.EVERYONE -> {
                        addComponent(
                            translatable("gui.join_while_running.lore.everyone")
                                .color(Colors.JOIN_WHILE_RUNNING_SECONDARY)
                                .render(locale)
                        )
                    }
                    AccessLevel.NONE -> {
                        addComponent(
                            translatable("gui.join_while_running.lore.none")
                                .color(Colors.JOIN_WHILE_RUNNING_SECONDARY)
                                .render(locale)
                        )
                    }
                }
                addComponent(
                    translatable(
                        "gui.join_while_running.lore",
                        listOf(
                            if (Timer.joinWhileRunning.contains(accessLevel)) {
                                translatable("generic.disable")
                                    .color(Colors.INACTIVE)
                            } else {
                                translatable("generic.activate")
                                    .color(Colors.ACTIVE)
                            }
                        )
                    )
                        .color(Colors.JOIN_WHILE_RUNNING_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
            )
        }
    }

fun generateItemForHide(player: Player, locale: Locale): ItemStack {
    val head = getPlayerHead(player)
    val meta = head.itemMeta

    meta.setLore {
        +space()
        if (player.hidden) {
            addComponent(
                translatable(
                    "gui.hide.hidden.lore",
                    listOf(player.name())
                )
                    .color(Colors.INACTIVE)
                    .render(locale)
            )
        } else {
            addComponent(
                translatable(
                    "gui.hide.revealed.lore",
                    listOf(player.name())
                )
                    .color(Colors.ACTIVE)
                    .render(locale)
            )
        }
    }

    meta.flags(
        ItemFlag.HIDE_ATTRIBUTES,
        ItemFlag.HIDE_DESTROYS,
        ItemFlag.HIDE_DYE,
        ItemFlag.HIDE_PLACED_ON,
        ItemFlag.HIDE_POTION_EFFECTS,
        ItemFlag.HIDE_UNBREAKABLE
    )

    if (player.hidden) {
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
    }
    head.itemMeta = meta
    return head
}

/* Other Items */

fun getPlayerHead(player: OfflinePlayer): ItemStack {
    val head = itemStack(Material.PLAYER_HEAD) {
        meta {
            name = text(player.name ?: " ")

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
            )
        }
    }
    val meta = head.itemMeta
    (meta as SkullMeta).owningPlayer = player
    head.itemMeta = meta
    return head
}

fun getTextureHead(b64: String, name: String): ItemStack =
    getTextureHead(b64, text(name))

fun getTextureHead(b64: String, name: Component): ItemStack {
    val head = itemStack(Material.PLAYER_HEAD) {
        meta {
            this.name = name

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
            )
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
