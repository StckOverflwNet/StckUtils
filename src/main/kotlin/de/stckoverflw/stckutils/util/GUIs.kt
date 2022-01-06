package de.stckoverflw.stckutils.util

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.command.HideCommand
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.extension.resetWorlds
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.gamechange.GameExtension
import de.stckoverflw.stckutils.minecraft.gamechange.GameRule
import de.stckoverflw.stckutils.minecraft.goal.Battle
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.minecraft.goal.TeamGoal
import de.stckoverflw.stckutils.minecraft.goal.active
import de.stckoverflw.stckutils.minecraft.timer.AccessLevel
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.minecraft.timer.TimerDirection
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.*
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

object GUIPage {
    const val goalsPage = 2
    const val gameChangesPage = 1
    const val settingsPageNumber = 0
    const val challengesPageNumber = -1
    const val moreSettingsPageNumber = -2
    const val timerColorPageNumber = -3
    const val timerPageNumber = -4
    const val worldResetPageNumber = -5
    const val hidePageNumber = -6
    const val timerJoinWhileRunningPageNumber = -7
}

/**
 * The Method to generate a new Instance of the Settings GUI
 */
fun settingsGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
    title = "§9Settings"
    defaultPage = GUIPage.settingsPageNumber

    // Default Settings Page
    page(GUIPage.settingsPageNumber) {
        // Placeholders at the Border of the Inventory
        placeholder(Slots.Border, placeHolderItemGray)
        // Placeholders in the Middle field of the Inventory
        placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

        // Item for opening the Challenges Page
        pageChanger(
            Slots.RowThreeSlotThree,
            itemStack(Material.DRAGON_HEAD) {
                meta {
                    name = StckUtilsPlugin.translationsProvider.translate(
                        "gui.page_changer.challenges.name",
                        locale,
                        "items"
                    )
                    addLore {
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.page_changer.challenges.lore",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            },
            GUIPage.challengesPageNumber, null, null
        )

        // Item for opening the GameChanges Page
        pageChanger(
            Slots.RowThreeSlotFive,
            itemStack(Material.FILLED_MAP) {
                meta {
                    name = StckUtilsPlugin.translationsProvider.translate(
                        "gui.page_changer.game_change.name",
                        locale,
                        "items"
                    )
                    addLore {
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.page_changer.game_change.lore",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            },
            GUIPage.gameChangesPage, null, null
        )

        // Item for opening the Goals Page
        pageChanger(
            Slots.RowThreeSlotSeven,
            itemStack(Material.DIAMOND) {
                meta {
                    name = StckUtilsPlugin.translationsProvider.translate(
                        "gui.page_changer.goals.name",
                        locale,
                        "items"
                    )
                    addLore {
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.page_changer.goals.lore",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            },
            GUIPage.goalsPage, null, null
        )

        // Item for opening the Page with More settings
        pageChanger(
            Slots.RowOneSlotFive,
            itemStack(Material.COMPARATOR) {
                meta {
                    name = StckUtilsPlugin.translationsProvider.translate(
                        "gui.page_changer.more_settings.name",
                        locale,
                        "items"
                    )
                    addLore {
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.page_changer.more_settings.lore",
                            locale,
                            "items",
                            arrayOf(KColors.ROSYBROWN.toString())
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            },
            GUIPage.moreSettingsPageNumber, null, null
        )
    }

    // More settings Page
    page(GUIPage.moreSettingsPageNumber) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_VERTICALLY
        this.transitionFrom = PageChangeEffect.SLIDE_VERTICALLY

        // Placeholders at the Border of the Inventory
        placeholder(Slots.Border, placeHolderItemGray)
        // Placeholders in the Middle field of the Inventory
        placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

        // Go back Item
        pageChanger(Slots.RowFiveSlotFive, getGoBackItem(locale), defaultPage, null, null)

        // Item for opening the World reset Settings Page
        pageChanger(
            Slots.RowThreeSlotThree,
            itemStack(Material.GRASS_BLOCK) {
                meta {
                    name = KColors.ROSYBROWN.toString().plus(
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.page_changer.world_reset.name",
                            locale,
                            "items"
                        )
                    )
                    addLore {
                        KColors.ROSYBROWN.toString().plus(
                            StckUtilsPlugin.translationsProvider.translate(
                                "gui.page_changer.world_reset.lore",
                                locale,
                                "items"
                            )
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            },
            GUIPage.worldResetPageNumber, null, null
        )

        // Item for opening the Timer Settings Page
        pageChanger(
            Slots.RowThreeSlotSeven,
            itemStack(Material.CLOCK) {
                meta {
                    name = StckUtilsPlugin.translationsProvider.translate(
                        "gui.page_changer.timer.name",
                        locale,
                        "items"
                    )
                    addLore {
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.page_changer.timer.lore",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            },
            GUIPage.timerPageNumber, null, null
        )
    }

    // Challenges Page
    page(GUIPage.challengesPageNumber) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY
        this.transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY

        // Placeholders at the left Border
        placeholder(Slots.Border, placeHolderItemGray)

        // Go back Item
        pageChanger(Slots.RowThreeSlotNine, getGoBackItem(locale), defaultPage, null, null)

        // Compound for displaying the Challenges
        val compound = createRectCompound<Challenge>(
            Slots.RowTwoSlotTwo, Slots.RowFourSlotEight,
            iconGenerator = {
                generateItemForChallenge(it, locale)
            },
            onClick = { clickEvent, challenge ->
                val player = clickEvent.player

                clickEvent.bukkitEvent.isCancelled = true
                if (clickEvent.bukkitEvent.isLeftClick) {
                    if (Timer.running) {
                        player.sendMessage(
                            StckUtilsPlugin.translationsProvider.translateWithPrefix(
                                "gui.timer_not_paused",
                                player.language,
                                "messages"
                            )
                        )
                    } else {
                        if (!(challenge.requiresProtocolLib && !StckUtilsPlugin.isProtocolLib)) {
                            challenge.active = !challenge.active
                            challenge.onToggle()
                            clickEvent.bukkitEvent.clickedInventory!!
                                .setItem(clickEvent.bukkitEvent.slot, generateItemForChallenge(challenge, locale))
                        } else {
                            player.sendMessage(
                                StckUtilsPlugin.translationsProvider.translateWithPrefix(
                                    "gui.depend.protocol_lib",
                                    player.language,
                                    "messages"
                                )
                            )
                        }
                    }
                } else if (clickEvent.bukkitEvent.isRightClick) {
                    val configGUI = challenge.configurationGUI(player.language)
                    if (configGUI != null) {
                        if (challenge.active) {
                            player.openGUI(configGUI)
                        } else {
                            player.sendMessage(
                                StckUtilsPlugin.translationsProvider.translateWithPrefix(
                                    "gui.challenge.config.challenge_not_activated",
                                    player.language,
                                    "messages"
                                )
                            )
                        }
                    }
                }
            }
        )
        compound.addContent(ChallengeManager.challenges)

        compoundScroll(
            Slots.RowOneSlotNine,
            getScrollDownItem(locale), compound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowFiveSlotNine,
            getScrollUpItem(locale), compound, scrollTimes = 1, reverse = true
        )
    }

    // GameChange Page
    page(GUIPage.gameChangesPage) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_VERTICALLY
        this.transitionFrom = PageChangeEffect.SLIDE_VERTICALLY

        // Placeholders at the Border
        placeholder(Slots.Border, placeHolderItemGray)

        // Placeholders in the Middle
        placeholder(Slots.RowThreeSlotTwo rectTo Slots.RowThreeSlotEight, placeHolderItemWhite)

        // Go back Item
        pageChanger(Slots.RowOneSlotFive, getGoBackItem(locale), defaultPage, null, null)

        // Compound for displaying the GameExtensions
        val gameExtensionCompound = createRectCompound<GameExtension>(
            Slots.RowFourSlotTwo, Slots.RowFourSlotEight,
            iconGenerator = {
                it.item(locale)
            },
            onClick = { clickEvent, extension ->
                clickEvent.bukkitEvent.isCancelled = true
                extension.click(clickEvent)
            }
        )

        // Compound for displaying the Minecraft GameRules
        val gameRuleCompound = createRectCompound<GameRule>(
            Slots.RowTwoSlotTwo, Slots.RowTwoSlotEight,
            iconGenerator = {
                it.item(locale)
            },
            onClick = { clickEvent, rule ->
                clickEvent.bukkitEvent.isCancelled = true
                rule.click(clickEvent)
            }
        )
        gameExtensionCompound
            .addContent(GameChangeManager.gameChanges.filterIsInstance<GameExtension>())
        gameRuleCompound
            .addContent(GameChangeManager.gameChanges.filterIsInstance<GameRule>())

        compoundScroll(
            Slots.RowFourSlotNine,
            getScrollRightItem(locale), gameRuleCompound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowFourSlotOne,
            getScrollLeftItem(locale), gameRuleCompound, scrollTimes = 1, reverse = true
        )

        compoundScroll(
            Slots.RowTwoSlotNine,
            getScrollRightItem(locale), gameExtensionCompound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowTwoSlotOne,
            getScrollLeftItem(locale), gameExtensionCompound, scrollTimes = 1, reverse = true
        )
    }

    // Goals Page
    page(GUIPage.goalsPage) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY
        this.transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY

        // Placeholder
        placeholder(Slots.RowOneSlotOne rectTo Slots.RowFiveSlotNine, placeHolderItemGray)

        // Go back Item
        pageChanger(Slots.RowThreeSlotOne, getGoBackItem(locale), defaultPage, null, null)

        placeholder(
            Slots.RowFourSlotOne,
            itemStack(Material.AZALEA) {
                meta {
                    name = StckUtilsPlugin.translationsProvider.translate(
                        "gui.team_goal.name",
                        locale,
                        "items"
                    )
                    addLore {
                        +" "
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.team_goal.lore",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            }
        )

        placeholder(
            Slots.RowTwoSlotOne,
            itemStack(Material.NETHERITE_SWORD) {
                meta {
                    name = StckUtilsPlugin.translationsProvider.translate(
                        "gui.battle.name",
                        locale,
                        "items"
                    )
                    addLore {
                        +" "
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.battle.lore",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            }
        )

        // Compound for the Goals
        val teamGoalCompound = createRectCompound<TeamGoal>(
            Slots.RowFourSlotFour, Slots.RowFourSlotEight,
            iconGenerator = {
                generateItemForGoal(it, locale)
            },
            onClick = { clickEvent, goal ->
                val player = clickEvent.player
                clickEvent.bukkitEvent.isCancelled = true
                if (clickEvent.bukkitEvent.isLeftClick) {
                    if (Timer.running) {
                        player.sendMessage(
                            StckUtilsPlugin.translationsProvider.translateWithPrefix(
                                "gui.timer_not_paused",
                                player.language,
                                "messages"
                            )
                        )
                    } else {
                        if (GoalManager.activeGoal != goal) {
                            GoalManager.goals.forEach {
                                it.active = it == goal
                            }
                            GoalManager.activeGoal = goal
                        } else {
                            GoalManager.goals.forEach {
                                it.active = false
                            }
                            GoalManager.activeGoal = null
                        }
                    }
                }

                clickEvent.guiInstance.reloadCurrentPage()
            }
        )

        // Compound for the Goals
        val battleCompound = createRectCompound<Battle>(
            Slots.RowTwoSlotFour, Slots.RowTwoSlotEight,
            iconGenerator = {
                generateItemForGoal(it, locale)
            },
            onClick = { clickEvent, goal ->
                clickEvent.bukkitEvent.isCancelled = true
                if (GoalManager.activeGoal != goal) {
                    GoalManager.activeGoal = goal
                } else {
                    GoalManager.activeGoal = null
                }
                clickEvent.guiInstance.reloadCurrentPage()
            }
        )
        battleCompound.addContent(GoalManager.goals.filterIsInstance<Battle>())
        teamGoalCompound.addContent(GoalManager.goals.filterIsInstance<TeamGoal>())

        compoundScroll(
            Slots.RowFourSlotNine,
            getScrollRightItem(locale), teamGoalCompound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowFourSlotThree,
            getScrollLeftItem(locale), teamGoalCompound, scrollTimes = 1, reverse = true
        )

        compoundScroll(
            Slots.RowTwoSlotNine,
            getScrollRightItem(locale), battleCompound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowTwoSlotThree,
            getScrollLeftItem(locale), battleCompound, scrollTimes = 1, reverse = true
        )
    }

    // Settings Page for the Timer
    page(GUIPage.timerPageNumber) {

        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_VERTICALLY
        this.transitionFrom = PageChangeEffect.SLIDE_VERTICALLY

        // Placeholders
        placeholder(Slots.Border, placeHolderItemGray)
        placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

        // go back Item
        pageChanger(Slots.RowFiveSlotFive, getGoBackItem(locale), GUIPage.moreSettingsPageNumber, null, null)

        // Item for starting/stopping the Timer
        button(
            Slots.RowFourSlotThree,
            generateStartStopTimerItem(locale)
        ) {
            if (Timer.running) {
                Timer.stop()
                Bukkit.broadcast(
                    text(
                        StckUtilsPlugin.translationsProvider.translateWithPrefix(
                            "timer.stopped",
                            locale,
                            "messages"
                        )
                    )
                )
            } else {
                Timer.start()
                Bukkit.broadcast(
                    text(
                        StckUtilsPlugin.translationsProvider.translateWithPrefix(
                            "timer.started",
                            locale,
                            "messages"
                        )
                    )
                )
            }
            it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateStartStopTimerItem(locale))
            it.bukkitEvent.clickedInventory!!.setItem(13, generateTimerItem(locale))
        }

        // Item for changing the Time
        button(
            Slots.RowFourSlotFive,
            generateTimerItem(locale)
        ) {
            it.bukkitEvent.isCancelled = true
            if (it.bukkitEvent.isLeftClick) {
                Timer.time += 60
            } else if (it.bukkitEvent.isRightClick) {
                if (Timer.time >= 60) {
                    Timer.time -= 60
                }
            }
            it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateTimerItem(locale))
        }

        // Item for toggling the only OP join when Timer is running
        pageChanger(
            Slots.RowTwoSlotFive,
            itemStack(Material.ENCHANTED_GOLDEN_APPLE) {
                meta {
                    name = StckUtilsPlugin.translationsProvider.translate(
                        "gui.page_changer.join_while_running.name",
                        locale,
                        "items"
                    )

                    setLore {
                        +" "
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.page_changer.join_while_running.lore",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            },
            GUIPage.timerJoinWhileRunningPageNumber,
            null,
            null
        )

        // Item for resetting the Timer
        button(
            Slots.RowTwoSlotThree,
            itemStack(Material.BARRIER) {
                meta {
                    name = StckUtilsPlugin.translationsProvider.translate(
                        "gui.reset_timer.name",
                        locale,
                        "items"
                    )
                    addLore {
                        +" "
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.reset_timer.lore",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            }
        ) {
            if (Timer.running) {
                Timer.stop()
            }
            Timer.reset()
            Bukkit.broadcast(
                text(
                    StckUtilsPlugin.translationsProvider.translateWithPrefix(
                        "timer.reset",
                        locale,
                        "messages"
                    )
                )
            )
            it.bukkitEvent.clickedInventory!!.setItem(13, generateTimerItem(locale))
        }

        // Item for changing to the Timer color page
        pageChanger(
            Slots.RowFourSlotSeven,
            itemStack(Material.ORANGE_DYE) {
                meta {
                    name = StckUtilsPlugin.translationsProvider.translate(
                        "gui.page_changer.color.name",
                        locale,
                        "items"
                    )
                    addLore {
                        +" "
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.page_changer.color.lore",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            },
            GUIPage.timerColorPageNumber,
            null,
            null
        )

        // Item for changing the Timer direction
        button(
            Slots.RowTwoSlotSeven,
            generateTimerDirectionItem(locale)
        ) {
            Timer.direction = when (Timer.direction) {
                TimerDirection.FORWARDS -> {
                    TimerDirection.BACKWARDS
                }
                TimerDirection.BACKWARDS -> {
                    TimerDirection.FORWARDS
                }
            }
            it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateTimerDirectionItem(locale))
        }
    }

    // Settings Page for the Timer color
    page(GUIPage.timerColorPageNumber) {

        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY
        this.transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY

        // Placeholders
        placeholder(Slots.Border, placeHolderItemGray)
        placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

        // go back Item
        pageChanger(Slots.RowThreeSlotOne, getGoBackItem(locale), GUIPage.timerPageNumber, null, null)

        // Color compound
        val compound = createRectCompound<ChatColor>(
            Slots.RowTwoSlotTwo, Slots.RowFourSlotEight,
            iconGenerator = { chatColor ->
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
                        name = "$chatColor${chatColor.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }}"

                        addLore {
                            +" "
                            StckUtilsPlugin.translationsProvider.translate(
                                "gui.color_compound.lore",
                                locale,
                                "items",
                                arrayOf(
                                    chatColor.toString().plus(
                                        chatColor.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }
                                    ),
                                    chatColor.toString().plus("§l").plus(
                                        ChatColor.stripColor(Timer.formatTime(90061.toLong())) // 1d 1h 1m 1s
                                    )
                                )
                            ).split("\n").forEach {
                                +it
                            }
                        }
                    }
                }
            },
            onClick = { clickEvent, chatColor ->
                clickEvent.bukkitEvent.isCancelled = true
                Timer.color = "§${chatColor.char}"
            }
        )

        compound.addContent(ChatColor.values().filter { it.isColor })
    }

    // Settings Page for World reset
    page(GUIPage.worldResetPageNumber) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_VERTICALLY
        this.transitionFrom = PageChangeEffect.SLIDE_VERTICALLY

        // Placeholders
        placeholder(Slots.Border, placeHolderItemGray)
        placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

        // Go back Item
        pageChanger(Slots.RowFiveSlotFive, getGoBackItem(locale), GUIPage.moreSettingsPageNumber, null, null)

        // Item for activating/deactivating Village Spawn
        button(Slots.RowThreeSlotThree, generateVillageSpawnItem(locale)) {
            Config.resetSettingsConfig.villageSpawn = !Config.resetSettingsConfig.villageSpawn
            it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateVillageSpawnItem(locale))
        }

        // Item for running a World reset
        button(
            Slots.RowThreeSlotSeven,
            itemStack(Material.BARRIER) {
                meta {
                    name = StckUtilsPlugin.translationsProvider.translate(
                        "gui.world_reset.name",
                        locale,
                        "items"
                    )
                    addLore {
                        +" "
                        StckUtilsPlugin.translationsProvider.translate(
                            "gui.world_reset.lore",
                            locale,
                            "items"
                        ).split("\n").forEach {
                            +it
                        }
                    }
                }
            }
        ) {
            it.player.resetWorlds()
        }
    }

    // Settings page for Hide functionality
    page(GUIPage.hidePageNumber) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_VERTICALLY
        this.transitionFrom = PageChangeEffect.SLIDE_VERTICALLY

        // Placeholders
        placeholder(Slots.Border, placeHolderItemGray)
        placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

        // Go back Item
        pageChanger(Slots.RowFiveSlotFive, getGoBackItem(locale), GUIPage.moreSettingsPageNumber, null, null)

        // Compound for displaying the players
        val compound = createRectCompound<Player>(
            Slots.RowOneSlotOne, Slots.RowFiveSlotEight,
            iconGenerator = {
                generateItemForHide(it, locale)
            },
            onClick = click@{ clickEvent, target ->
                clickEvent.bukkitEvent.isCancelled = true
                val player = clickEvent.player
                val perm = if (player == target) {
                    Permissions.HIDE_SELF
                } else {
                    Permissions.HIDE_OTHER
                }
                if (!player.hasPermission(perm)) {
                    return@click player.sendMessage(
                        StckUtilsPlugin.translationsProvider.translateWithPrefix(
                            "generic.missing_permission",
                            player.language,
                            "general",
                            arrayOf(perm)
                        )
                    )
                }
                HideCommand.sendResponse(player, target)
                clickEvent.bukkitEvent.currentItem = generateItemForHide(target, locale)
            }
        )
        compound.addContent(onlinePlayers)

        compoundScroll(
            Slots.RowOneSlotNine,
            getScrollDownItem(locale), compound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowFiveSlotNine,
            getScrollUpItem(locale), compound, scrollTimes = 1, reverse = true
        )
    }

    // Settings page for join while Timer is Running functionality
    page(GUIPage.timerJoinWhileRunningPageNumber) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_VERTICALLY
        this.transitionFrom = PageChangeEffect.SLIDE_VERTICALLY

        // Placeholders
        placeholder(Slots.Border, placeHolderItemGray)
        placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

        // Go back Item
        pageChanger(Slots.RowFiveSlotFive, getGoBackItem(locale), GUIPage.timerPageNumber, null, null)

        val compound = createRectCompound<AccessLevel>(
            Slots.RowFourSlotTwo,
            Slots.RowFourSlotEight,
            iconGenerator = {
                generateItemForJoinWhileRunning(it, locale)
            },
            onClick = { clickEvent, accessLevel ->
                Timer.joinWhileRunning = if (Timer.joinWhileRunning.contains(accessLevel)) {
                    Timer.joinWhileRunning.minus(accessLevel)
                } else {
                    when (accessLevel) {
                        AccessLevel.OPERATOR,
                        AccessLevel.HIDDEN -> {
                            Timer.joinWhileRunning.minus(AccessLevel.NONE).minus(AccessLevel.EVERYONE).plus(accessLevel)
                        }
                        AccessLevel.EVERYONE -> {
                            Timer.joinWhileRunning.minus(AccessLevel.values().toSet()).plus(accessLevel)
                        }
                        AccessLevel.NONE -> {
                            Timer.joinWhileRunning.minus(AccessLevel.values().toSet()).plus(accessLevel)
                        }
                    }
                }
                if (Timer.joinWhileRunning.isEmpty()) {
                    Timer.joinWhileRunning = Timer.joinWhileRunning.plus(AccessLevel.OPERATOR)
                }
                clickEvent.bukkitEvent.isCancelled = true
                clickEvent.guiInstance.reloadCurrentPage()
            }
        )
        compound.addContent(AccessLevel.values().toList())

        compoundScroll(
            Slots.RowOneSlotNine,
            getScrollDownItem(locale), compound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowFiveSlotNine,
            getScrollUpItem(locale), compound, scrollTimes = 1, reverse = true
        )
    }
}
