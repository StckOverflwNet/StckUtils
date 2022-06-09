package de.stckoverflw.stckutils.util

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.hidden
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
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.bukkit.plainText
import net.axay.kspigot.extensions.bukkit.render
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
import net.kyori.adventure.text.format.NamedTextColor
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
        literalText {
            component(
                translatable("gui.scroll_right")
                    .render(locale)
            )
            color = Colors.ACCENT_AQUA
        }
    )

fun getScrollLeftItem(locale: Locale) =
    getTextureHead(
        Textures.SCROLL_LEFT_B64,
        literalText {
            component(
                translatable("gui.scroll_left")
                    .render(locale)
            )
            color = Colors.ACCENT_AQUA
        }
    )

fun getScrollUpItem(locale: Locale) =
    getTextureHead(
        Textures.SCROLL_UP_B64,
        literalText {
            component(
                translatable("gui.scroll_up")
                    .render(locale)
            )
            color = Colors.ACCENT_AQUA
        }
    )

fun getScrollDownItem(locale: Locale) =
    getTextureHead(
        Textures.SCROLL_DOWN_B64,
        literalText {
            component(
                translatable("gui.scroll_down")
                    .render(locale)
            )
            color = Colors.ACCENT_AQUA
        }
    )

fun getGoBackItem(locale: Locale) =
    itemStack(Material.KNOWLEDGE_BOOK) {
        meta {
            name = literalText {
                component(
                    translatable("gui.back.name")
                        .render(locale)
                )
                color = Colors.GO_BACK
            }

            addLore {
                addComponent(
                    literalText {
                        newLine()
                        component(
                            translatable("gui.back.lore")
                                .render(locale)
                        )
                        color = Colors.GO_BACK_SECONDARY
                    }
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

fun getSettingsItem(locale: Locale) =
    itemStack(Material.NETHER_STAR) {
        meta {
            name = literalText {
                component(
                    translatable("settings.name")
                        .render(locale)
                )
                color = Colors.SETTINGS
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

fun generateChallengeGuiItem(locale: Locale) =
    itemStack(Material.DRAGON_HEAD) {
        meta {
            name = literalText {
                component(
                    translatable("gui.page_changer.challenges.name")
                        .render(locale)
                )
                color = Colors.CHALLENGE
            }

            addLore {
                addComponent(
                    literalText {
                        component(
                            translatable("gui.page_changer.challenges.lore")
                                .render(locale)
                        )
                        color = Colors.CHALLENGE_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable("gui.page_changer.game_change.name")
                        .render(locale)
                )
                color = Colors.GAME_CHANGE
            }

            addLore {
                addComponent(
                    literalText {
                        component(
                            translatable("gui.page_changer.game_change.lore")
                                .render(locale)
                        )
                        color = Colors.GAME_CHANGE_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable("gui.page_changer.goals.name")
                        .render(locale)
                )
                color = Colors.GOAL
            }

            addLore {
                addComponent(
                    literalText {
                        component(
                            translatable("gui.page_changer.goals.lore")
                                .render(locale)
                        )
                        color = Colors.GOAL_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable("gui.page_changer.more_settings.name")
                        .render(locale)
                )
                color = Colors.MORE_SETTINGS
            }

            addLore {
                addComponent(
                    literalText {
                        component(
                            translatable("gui.page_changer.more_settings.lore")
                                .render(locale)
                        )
                        color = Colors.MORE_SETTINGS_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable("gui.page_changer.world_reset.name")
                        .render(locale)
                )
                color = Colors.WORLD_RESET_GUI
            }

            addLore {
                addComponent(
                    literalText {
                        component(
                            translatable("gui.page_changer.world_reset.lore")
                                .render(locale)
                        )
                        color = Colors.WORLD_RESET_GUI_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable("gui.page_changer.timer.name")
                        .render(locale)
                )
                color = Colors.TIMER_SETTINGS
            }

            addLore {
                addComponent(
                    literalText {
                        component(
                            translatable("gui.page_changer.timer.lore")
                                .render(locale)
                        )
                        color = Colors.TIMER_SETTINGS_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable("gui.team_goal.name")
                        .render(locale)
                )
                color = Colors.GOAL_TYPE
            }

            addLore {
                addComponent(
                    literalText {
                        newLine()
                        component(
                            translatable("gui.team_goal.lore")
                                .render(locale)
                        )
                        color = Colors.GOAL_TYPE_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable("gui.battle.name")
                        .render(locale)
                )
                color = Colors.GOAL_TYPE
            }

            addLore {
                addComponent(
                    literalText {
                        newLine()
                        component(
                            translatable("gui.battle.lore")
                                .render(locale)
                        )
                        color = Colors.GOAL_TYPE_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable("gui.page_changer.join_while_running.name")
                        .render(locale)
                )
                color = Colors.JOIN_WHILE_RUNNING
            }

            setLore {
                addComponent(
                    literalText {
                        newLine()
                        component(
                            translatable("gui.page_changer.join_while_running.lore")
                                .render(locale)
                        )
                        color = Colors.JOIN_WHILE_RUNNING_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable("gui.reset_timer.name")
                        .render(locale)
                )
                color = Colors.RESET
            }

            addLore {
                addComponent(
                    literalText {
                        newLine()
                        component(
                            translatable("gui.reset_timer.lore")
                                .render(locale)
                        )
                        color = Colors.RESET_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable("gui.page_changer.color.name")
                        .render(locale)
                )
                color = Colors.TIMER_COLOR
            }

            addLore {
                addComponent(
                    literalText {
                        newLine()
                        component(
                            translatable("gui.page_changer.color.lore")
                                .render(locale)
                        )
                        color = Colors.TIMER_COLOR_SECONDARY
                    }
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

fun generateColorCompoundItem(textColor: NamedTextColor, locale: Locale) =
    itemStack(
        when (textColor) {
            NamedTextColor.DARK_RED -> Material.RED_DYE
            NamedTextColor.RED -> Material.RED_DYE
            NamedTextColor.GOLD -> Material.ORANGE_DYE
            NamedTextColor.YELLOW -> Material.YELLOW_DYE
            NamedTextColor.DARK_GREEN -> Material.GREEN_DYE
            NamedTextColor.GREEN -> Material.GREEN_DYE
            NamedTextColor.AQUA -> Material.CYAN_DYE
            NamedTextColor.DARK_AQUA -> Material.CYAN_DYE
            NamedTextColor.DARK_BLUE -> Material.BLUE_DYE
            NamedTextColor.BLUE -> Material.LIGHT_BLUE_DYE
            NamedTextColor.LIGHT_PURPLE -> Material.PURPLE_DYE
            NamedTextColor.DARK_PURPLE -> Material.PURPLE_DYE
            NamedTextColor.WHITE -> Material.WHITE_DYE
            NamedTextColor.GRAY -> Material.LIGHT_GRAY_DYE
            NamedTextColor.DARK_GRAY -> Material.GRAY_DYE
            else -> Material.BLACK_DYE
        }
    ) {
        meta {
            name = literalText {
                text(textColor.name())
                color = textColor
            }

            addLore {
                addComponent(
                    literalText {
                        newLine()
                        component(
                            translatable("gui.color_compound.lore")
                                .args(
                                    literalText {
                                        text(textColor.name())
                                        color = textColor
                                    },
                                    literalText {
                                        text(Timer.formatTime(90061L, locale).plainText()) // 1d 1h 1m 1s
                                        color = textColor
                                    }
                                )
                                .render(locale)
                        )
                        color = Colors.COLOR_COMPOUND_SECONDARY
                    }
                )
            }

            persistentDataContainer.set(Namespaces.COLOR_COMPOUND_VALUE, textColor.value())

            if (Timer.color == textColor) {
                addEnchant(Enchantment.ARROW_DAMAGE, 1, false)
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_ENCHANTS
            )
        }
    }

private fun NamedTextColor.name() = if (toString().contains("_")) {
    toString().substringBefore('_').lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }.plus(" ")
        .plus(toString().substringAfter('_').lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) })
} else {
    toString().lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }
}

fun generateWorldResetItem(locale: Locale) =
    itemStack(Material.BARRIER) {
        meta {
            name = literalText {
                component(
                    translatable("gui.world_reset.name")
                        .render(locale)
                )
                color = Colors.WORLD_RESET
            }

            addLore {
                addComponent(
                    literalText {
                        newLine()
                        component(
                            translatable("gui.world_reset.lore")
                                .render(locale)
                        )
                        color = Colors.WORLD_RESET_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable(challenge.nameKey)
                        .render(locale)
                )
                color = Colors.CHALLENGE_COMPOUND
            }

            addLore {
                addComponent(
                    literalText {
                        component(
                            translatable(challenge.descriptionKey)
                                .render(locale)
                        )
                        color = Colors.CHALLENGE_COMPOUND_SECONDARY
                        component(space())
                    }
                )
                if (!(challenge.requiresProtocolLib && !StckUtilsPlugin.isProtocolLib)) {
                    if (challenge.active) {
                        addComponent(
                            literalText {
                                component(
                                    translatable("gui.challenge.lore.active")
                                        .render(locale)
                                )
                                color = Colors.ACTIVE
                            }
                        )
                    } else {
                        addComponent(
                            literalText {
                                component(
                                    translatable("gui.challenge.lore.inactive")
                                        .render(locale)
                                )
                                color = Colors.INACTIVE
                            }
                        )
                    }
                    if (challenge.configurationGUI(Config.languageConfig.defaultLanguage) != null) {
                        addComponent(
                            literalText {
                                component(
                                    translatable("gui.challenge.lore.config_gui")
                                        .args(
                                            literalText {
                                                component(
                                                    translatable(challenge.nameKey)
                                                )
                                                color = Colors.CONFIGURATION
                                            }
                                        )
                                        .render(locale)
                                )
                                color = KColors.GRAY
                            }
                        )
                    }
                } else {
                    addComponent(
                        literalText {
                            component(
                                translatable("gui.challenge.lore.protocol_lib")
                                    .render(locale)
                            )
                            color = Colors.ERROR
                            bold = true
                        }
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
            name = literalText {
                component(
                    translatable(goal.nameKey)
                        .render(locale)
                )
                color = Colors.GOAL_COMPOUND
            }

            addLore {
                addComponent(
                    literalText {
                        component(
                            translatable(goal.descriptionKey)
                                .render(locale)
                        )
                        color = Colors.GOAL_COMPOUND_SECONDARY
                    }
                )
                +space()
                if (GoalManager.activeGoal == goal || goal.active) {
                    GoalManager.activeGoal = goal
                    addComponent(
                        literalText {
                            component(
                                translatable("gui.goal.lore.active")
                                    .render(locale)
                            )
                            color = Colors.ACTIVE
                        }
                    )
                } else {
                    addComponent(
                        literalText {
                            component(
                                translatable("gui.goal.lore.inactive")
                                    .render(locale)
                            )
                            color = Colors.INACTIVE
                        }
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
            name = literalText {
                component(
                    translatable("gui.timer.name")
                        .render(locale)
                )
                color = Colors.TIMER_ITEM
            }

            addLore {
                addComponent(
                    literalText {
                        newLine()
                        component(
                            translatable("gui.timer.lore")
                                .args(
                                    literalText {
                                        text(Timer.toString())
                                        color = Timer.color
                                    }
                                )
                                .render(locale)
                        )
                        color = Colors.TIMER_ITEM_SECONDARY
                    }
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
            name = literalText {
                component(
                    translatable("gui.village_spawn.name")
                )
                color = Colors.VILLAGE_SPAWN
            }

            addLore {
                addComponent(
                    literalText {
                        newLine()
                        component(
                            translatable(
                                if (Config.resetSettingsConfig.villageSpawn) {
                                    "gui.village_spawn.lore.activated"
                                } else {
                                    "gui.village_spawn.lore.deactivated"
                                }
                            )
                                .render(locale)
                        )
                        color = Colors.VILLAGE_SPAWN_SECONDARY
                    }
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

fun generateStartStopTimerItem(locale: Locale) =
    if (Timer.running) {
        itemStack(Material.REDSTONE) {
            meta {
                name = literalText {
                    component(
                        translatable("gui.start_stop_timer.running.name")
                            .render(locale)
                    )
                    color = Colors.INACTIVE
                }

                addLore {
                    addComponent(
                        literalText {
                            newLine()
                            component(
                                translatable("gui.start_stop_timer.running.lore")
                                    .render(locale)
                            )
                            color = KColors.GRAY
                        }
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
                name = literalText {
                    component(
                        translatable("gui.start_stop_timer.stopped.name")
                            .render(locale)
                    )
                    color = Colors.ACTIVE
                }

                addLore {
                    addComponent(
                        literalText {
                            newLine()
                            component(
                                translatable("gui.start_stop_timer.stopped.lore")
                                    .render(locale)
                            )
                            color = KColors.GRAY
                        }
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
            name = literalText {
                component(
                    translatable("gui.timer_direction.name")
                        .render(locale)
                )
                color = Colors.TIMER_DIRECTION
            }

            setLore {
                addComponent(
                    literalText {
                        newLine()
                        when (Timer.direction) {
                            TimerDirection.FORWARDS -> {
                                component(
                                    translatable("gui.timer_direction.lore.forwards")
                                        .render(locale)
                                )
                                color = Colors.ACTIVE
                            }
                            TimerDirection.BACKWARDS -> {
                                component(
                                    translatable("gui.timer_direction.lore.backwards")
                                        .render(locale)
                                )
                                color = Colors.INACTIVE
                            }
                        }
                    }
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

fun generateItemForJoinWhileRunning(accessLevel: AccessLevel, locale: Locale) =
    itemStack(
        if (Timer.joinWhileRunning.contains(accessLevel)) {
            Material.GREEN_WOOL
        } else {
            Material.RED_WOOL
        }
    ) {
        meta {
            name = literalText {
                text(
                    accessLevel.name.replace("_", " ")
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                )
                color = Colors.JOIN_WHILE_RUNNING
            }

            setLore {
                addComponent(
                    literalText {
                        newLine()
                        component(
                            translatable(
                                when (accessLevel) {
                                    AccessLevel.OPERATOR -> "gui.join_while_running.lore.operator"
                                    AccessLevel.HIDDEN -> "gui.join_while_running.lore.hidden"
                                    AccessLevel.EVERYONE -> "gui.join_while_running.lore.everyone"
                                    AccessLevel.NONE -> "gui.join_while_running.lore.none"
                                }
                            )
                                .render(locale)
                        )
                        color = Colors.JOIN_WHILE_RUNNING_SECONDARY
                        newLine()
                        component(
                            translatable("gui.join_while_running.lore")
                                .args(
                                    literalText {
                                        if (Timer.joinWhileRunning.contains(accessLevel)) {
                                            component(translatable("generic.disable"))
                                            color = Colors.INACTIVE
                                        } else {
                                            component(translatable("generic.activate"))
                                            color = Colors.ACTIVE
                                        }
                                    }
                                )
                                .render(locale)
                        )
                    }
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
    head.meta {
        setLore {
            addComponent(
                literalText {
                    newLine()
                    if (player.hidden) {
                        color = Colors.INACTIVE
                        component(
                            translatable("gui.hide.hidden.lore")
                                .args(player.name())
                                .render(locale)
                        )
                    } else {
                        color = Colors.ACTIVE
                        component(
                            translatable("gui.hide.revealed.lore")
                                .args(player.name())
                                .render(locale)
                        )
                    }
                }
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

        if (player.hidden) {
            addEnchant(Enchantment.ARROW_INFINITE, 1, true)
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }
    return head
}

/* Other Items */

fun getPlayerHead(player: OfflinePlayer): ItemStack =
    itemStack(Material.PLAYER_HEAD) {
        meta {
            name = literalText {
                Component.text(player.name ?: " ")
                color = Colors.PLAYER_HEAD
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
            )
            (this as SkullMeta).owningPlayer = player
        }
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
