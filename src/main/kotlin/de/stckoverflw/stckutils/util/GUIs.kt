package de.stckoverflw.stckutils.util

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.hidden
import de.stckoverflw.stckutils.extension.hide
import de.stckoverflw.stckutils.extension.resetWorlds
import de.stckoverflw.stckutils.extension.reveal
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
import net.kyori.adventure.text.Component
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
fun settingsGUI(): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
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
                    name = "§6Challenges"
                    addLore {
                        +"§6Challenges§7: change the Minecraft Gameplay"
                        +" "
                        +"§7Click to open the Challenge Inventory"
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
                    name = "§cGame Changes"
                    addLore {
                        +"§cGame Changes§7: change basic Game mechanics or"
                        +"§7add features to the basic Game"
                        +" "
                        +"§7Click to open the Game Change Inventory"
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
                    name = "§bGoals"
                    addLore {
                        +"§bGoals§7, the Goal you have while"
                        +"§7playing. The Challenge ends automatically"
                        +"§7when this goal is reached"
                        +" "
                        +"§7Click to open the Goal Inventory"
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
                    name = "§cMore Settings"
                    addLore {
                        +"§7Click to see §cmore settings§7, like Settings"
                        +"§7for the §eTimer §7or ${KColors.ROSYBROWN}World Reset"
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
        pageChanger(Slots.RowFiveSlotFive, goBackItem, defaultPage, null, null)

        // Item for opening the World reset Settings Page
        pageChanger(
            Slots.RowThreeSlotThree,
            itemStack(Material.GRASS_BLOCK) {
                meta {
                    name = "${KColors.ROSYBROWN}World Reset"
                    addLore {
                        +"${KColors.ROSYBROWN}World Reset§7: reset the"
                        +"§7in game world"
                        +" "
                        +"§7Click to open the Challenge Inventory"
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
                    name = "§eTimer"
                    addLore {
                        +"§eTimer§7: a simple Timer that counts"
                        +"§7upwards in seconds, minutes and hours"
                        +" "
                        +"§7Click to open the Timer Inventory"
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
        pageChanger(Slots.RowThreeSlotNine, goBackItem, defaultPage, null, null)

        // Compound for displaying the Challenges
        val compound = createRectCompound<Challenge>(
            Slots.RowTwoSlotTwo, Slots.RowFourSlotEight,
            iconGenerator = {
                generateItemForChallenge(it)
            },
            onClick = { clickEvent, challenge ->
                val player = clickEvent.player

                clickEvent.bukkitEvent.isCancelled = true
                if (clickEvent.bukkitEvent.isLeftClick) {
                    if (Timer.running) {
                        player.sendMessage(StckUtilsPlugin.prefix + "§cThe Timer has to be paused to do this")
                    } else {
                        if (!(challenge.requiresProtocolLib && !StckUtilsPlugin.isProtocolLib)) {
                            challenge.active = !challenge.active
                            challenge.onToggle()
                            clickEvent.bukkitEvent.clickedInventory!!
                                .setItem(clickEvent.bukkitEvent.slot, generateItemForChallenge(challenge))
                        } else {
                            player.sendMessage(StckUtilsPlugin.prefix + "§cInstall ProtocolLib to use this Challenge")
                        }
                    }
                } else if (clickEvent.bukkitEvent.isRightClick) {
                    val configGUI = challenge.configurationGUI()
                    if (configGUI != null) {
                        if (challenge.active) {
                            player.openGUI(configGUI)
                        } else {
                            player.sendMessage(
                                StckUtilsPlugin.prefix +
                                    "§cYou need to activate the Challenge before you can configure it"
                            )
                        }
                    }
                }
            }
        )
        compound.addContent(ChallengeManager.challenges)

        compoundScroll(
            Slots.RowOneSlotNine,
            scrollDownItem, compound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowFiveSlotNine,
            scrollUpItem, compound, scrollTimes = 1, reverse = true
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
        pageChanger(Slots.RowOneSlotFive, goBackItem, defaultPage, null, null)

        // Compound for displaying the GameExtensions
        val gameExtensionCompound = createRectCompound<GameExtension>(
            Slots.RowFourSlotTwo, Slots.RowFourSlotEight,
            iconGenerator = {
                it.item()
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
                it.item()
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
            scrollRightItem, gameRuleCompound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowFourSlotOne,
            scrollLeftItem, gameRuleCompound, scrollTimes = 1, reverse = true
        )

        compoundScroll(
            Slots.RowTwoSlotNine,
            scrollRightItem, gameExtensionCompound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowTwoSlotOne,
            scrollLeftItem, gameExtensionCompound, scrollTimes = 1, reverse = true
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
        pageChanger(Slots.RowThreeSlotOne, goBackItem, defaultPage, null, null)

        placeholder(
            Slots.RowFourSlotTwo,
            itemStack(Material.AZALEA) {
                meta {
                    name = "§aTeam Goal"
                    addLore {
                        +" "
                        +"§7Play together/alone for a Goal"
                        +"§7If one Player reaches the Goal"
                        +"§aeveryone §7wins the Challenge"
                    }
                }
            }
        )

        placeholder(
            Slots.RowTwoSlotTwo,
            itemStack(Material.NETHERITE_SWORD) {
                meta {
                    name = "§cBattle"
                    addLore {
                        +" "
                        +"§7Everyone §cfights §7for a Goal,"
                        +"§7The first Player reaching the Goal"
                        +"§7wins the Challenge"
                    }
                }
            }
        )

        // Compound for the Goals
        val teamGoalCompound = createRectCompound<TeamGoal>(
            Slots.RowFourSlotFour, Slots.RowFourSlotEight,
            iconGenerator = {
                generateItemForGoal(it)
            },
            onClick = { clickEvent, goal ->
                val player = clickEvent.player
                clickEvent.bukkitEvent.isCancelled = true
                if (clickEvent.bukkitEvent.isLeftClick) {
                    if (Timer.running) {
                        player.sendMessage(StckUtilsPlugin.prefix + "§cThe Timer has to be paused to do this")
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
                generateItemForGoal(it)
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
            scrollRightItem, teamGoalCompound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowFourSlotOne,
            scrollLeftItem, teamGoalCompound, scrollTimes = 1, reverse = true
        )

        compoundScroll(
            Slots.RowTwoSlotNine,
            scrollRightItem, battleCompound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowTwoSlotOne,
            scrollLeftItem, battleCompound, scrollTimes = 1, reverse = true
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
        pageChanger(Slots.RowFiveSlotFive, goBackItem, GUIPage.moreSettingsPageNumber, null, null)

        // Item for starting/stopping the Timer
        button(
            Slots.RowFourSlotThree,
            generateStartStopTimerItem()
        ) {
            if (Timer.running) {
                Timer.stop()
                Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §6stopped"))
            } else {
                Timer.start()
                Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §astarted"))
            }
            it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateStartStopTimerItem())
            it.bukkitEvent.clickedInventory!!.setItem(13, generateTimerItem())
        }

        // Item for changing the Time
        button(
            Slots.RowFourSlotFive,
            generateTimerItem()
        ) {
            it.bukkitEvent.isCancelled = true
            if (it.bukkitEvent.isLeftClick) {
                Timer.time += 60
            } else if (it.bukkitEvent.isRightClick) {
                if (Timer.time >= 60) {
                    Timer.time -= 60
                }
            }
            it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateTimerItem())
        }

        // Item for toggling the only OP join when Timer is running
        pageChanger(
            Slots.RowTwoSlotFive,
            itemStack(Material.ENCHANTED_GOLDEN_APPLE) {
                meta {
                    name = "§7Join While Timer is Running"

                    setLore {
                        +" "
                        +"§7click to §fconfigure"
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
                    name = "§cReset the Timer"
                    addLore {
                        +" "
                        +"§7Click to Reset the Timer"
                    }
                }
            }
        ) {
            if (Timer.running) {
                Timer.stop()
            }
            Timer.reset()
            Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §creset"))
            it.bukkitEvent.clickedInventory!!.setItem(13, generateTimerItem())
        }

        // Item for changing to the Timer color page
        pageChanger(
            Slots.RowFourSlotSeven,
            itemStack(Material.ORANGE_DYE) {
                meta {
                    name = "§aChange the Color"
                    addLore {
                        +" "
                        +"§7Change the display color"
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
            generateTimerDirectionItem()
        ) {
            Timer.direction = when (Timer.direction) {
                TimerDirection.FORWARDS -> {
                    TimerDirection.BACKWARDS
                }
                TimerDirection.BACKWARDS -> {
                    TimerDirection.FORWARDS
                }
            }
            it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateTimerDirectionItem())
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
        pageChanger(Slots.RowThreeSlotOne, goBackItem, GUIPage.timerPageNumber, null, null)

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
                            +"§7LMB - Change the color to $chatColor${
                            chatColor.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }
                            }"
                            +"§7Example Timer: $chatColor§l${ChatColor.stripColor(Timer.formatTime(90061.toLong()))}" // 1d 1h 1m 1s
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
        pageChanger(Slots.RowFiveSlotFive, goBackItem, GUIPage.moreSettingsPageNumber, null, null)

        // Item for activating/deactivating Village Spawn
        button(Slots.RowThreeSlotThree, generateVillageSpawnItem()) {
            Config.resetSettingsConfig.villageSpawn = !Config.resetSettingsConfig.villageSpawn
            it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateVillageSpawnItem())
        }

        // Item for running a World reset
        button(
            Slots.RowThreeSlotSeven,
            itemStack(Material.BARRIER) {
                meta {
                    name = "§cReset World"
                    addLore {
                        +" "
                        +"§7Click to §creset §7the World"
                        +"§7All Progress will be gone and"
                        +"§7the Timer will be set to 0"
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
        pageChanger(Slots.RowFiveSlotFive, goBackItem, GUIPage.moreSettingsPageNumber, null, null)

        // Compound for displaying the players
        val compound = createRectCompound<Player>(
            Slots.RowOneSlotOne, Slots.RowFiveSlotEight,
            iconGenerator = {
                generateItemForHide(it)
            },
            onClick = click@{ clickEvent, player ->
                clickEvent.bukkitEvent.isCancelled = true
                val perm = if (clickEvent.player == player) {
                    Permissions.HIDE_SELF
                } else {
                    Permissions.HIDE_OTHER
                }
                if (!clickEvent.player.hasPermission(perm)) {
                    return@click player.sendMessage(StckUtilsPlugin.prefix + "§cMissing permission: $perm")
                }
                if (player.hidden) {
                    player.reveal()
                    player.sendMessage(StckUtilsPlugin.prefix + "§ayou were revealed" + if (clickEvent.player != player) " by ${player.name}" else "")
                    player.sendMessage(StckUtilsPlugin.prefix + "§arevealed ${player.name}")
                } else {
                    player.hide()
                    player.sendMessage(StckUtilsPlugin.prefix + "§ayou were hidden" + if (clickEvent.player != player) " by ${player.name}" else "")
                    player.sendMessage(StckUtilsPlugin.prefix + "§ahid ${player.name}")
                }
                clickEvent.bukkitEvent.currentItem = generateItemForHide(player)
            }
        )
        compound.addContent(onlinePlayers)

        compoundScroll(
            Slots.RowOneSlotNine,
            scrollDownItem, compound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowFiveSlotNine,
            scrollUpItem, compound, scrollTimes = 1, reverse = true
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
        pageChanger(Slots.RowFiveSlotFive, goBackItem, GUIPage.timerPageNumber, null, null)

        val compound = createRectCompound<AccessLevel>(
            Slots.RowFourSlotTwo,
            Slots.RowFourSlotEight,
            iconGenerator = {
                generateItemForJoinWhileRunning(it)
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
            scrollDownItem, compound, scrollTimes = 1
        )
        compoundScroll(
            Slots.RowFiveSlotNine,
            scrollUpItem, compound, scrollTimes = 1, reverse = true
        )
    }
}
