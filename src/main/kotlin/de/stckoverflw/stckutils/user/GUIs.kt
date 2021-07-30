package de.stckoverflw.stckutils.user

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.resetWorlds
import de.stckoverflw.stckutils.minecraft.gamechange.GameChange
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.gamechange.active
import de.stckoverflw.stckutils.minecraft.goal.Goal
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.minecraft.timer.Timer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material

/**
 * The Method to generate a new Instance of the Settings GUI
 */
fun settingsGUI(): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
    title = "§9Settings"
    defaultPage = 1

    // Default Settings Page
    page(1) {
        // Placeholders at the Border of the Inventory
        placeholder(Slots.Border, placeHolderItemGray)
        // Placeholders in the Middle field of the Inventory
        placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

        // Item for opening the Challenges Page
        pageChanger(Slots.RowThreeSlotThree, itemStack(Material.DRAGON_HEAD) {
            meta {
                name = "§6Challenges"
                addLore {
                    + "§6Challenges§7, change the Minecraft Gameplay"
                    + " "
                    + "§7Click to open the Challenge Inventory"
                }
            }
        }, 2, null, null)

        // Item for opening the GameChanges Page
        pageChanger(Slots.RowThreeSlotFive, itemStack(Material.FILLED_MAP) {
            meta {
                name = "§cGame Changes"
                addLore {
                    + "§cGame Changes§7, change basic Game mechanics or"
                    + "§7add features to the basic Game"
                    + " "
                    + "§7Click to open the Challenge Inventory"
                }
            }
        }, 3, null, null)

        // Item for opening the Goals Page
        pageChanger(Slots.RowThreeSlotSeven, itemStack(Material.DIAMOND) {
            meta {
                name = "§bGoals"
                addLore {
                    + "§bGoals§7, the Goal you have while"
                    + "§7playing. The Challenge ends automatically"
                    + "§7when this goal is reached"
                    + " "
                    + "§7Click to open the Goal Inventory"
                }
            }
        }, 4, null, null)

        // Item for opening the Page with More settings
        pageChanger(Slots.RowOneSlotFive, itemStack(Material.COMPARATOR) {
            meta {
                name = "§cMore Settings"
                addLore {
                    + "§7Click to see §cmore settings§7, like Settings"
                    + "§7for the §eTimer §7or ${KColors.ROSYBROWN}World Reset"
                }
            }
        }, 0, null, null)
    }

    // More settings Page
    page(0) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_VERTICALLY
        this.transitionFrom = PageChangeEffect.SLIDE_VERTICALLY

        // Placeholders at the Border of the Inventory
        placeholder(Slots.Border, placeHolderItemGray)
        // Placeholders in the Middle field of the Inventory
        placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

        // Go back Item
        pageChanger(Slots.RowFiveSlotFive, goBackItem, 1, null, null)

        // Item for opening the World reset Settings Page
        pageChanger(Slots.RowThreeSlotFour, itemStack(Material.GRASS_BLOCK) {
            meta {
                name = "${KColors.ROSYBROWN}World Reset"
                addLore {
                    + "${KColors.ROSYBROWN}World Reset§7, reset the world"
                    + "§7in game"
                    + " "
                    + "§7Click to open the Challenge Inventory"
                }
            }
        }, -2, null, null)

        // Item for opening the Timer Settings Page
        pageChanger(Slots.RowThreeSlotSix, itemStack(Material.CLOCK) {
            meta {
                name = "§eTimer"
                addLore {
                    + "§eTimer§7, a simple Timer that counts"
                    + "§7upwards in seconds, minutes and hours"
                    + " "
                    + "§7Click to open the Timer Inventory"
                }
            }
        }, -1, null, null)
    }

    // Challenges Page
    page(2) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY
        this.transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY

        // Placeholders at the left Border
        placeholder(Slots.Border, placeHolderItemGray)

        // Go back Item
        pageChanger(Slots.RowThreeSlotOne, goBackItem, 1, null, null)

        // Compound for displaying the Challenges
        val compound = createRectCompound<Challenge>(
            Slots.RowOneSlotTwo, Slots.RowFiveSlotNine,
            iconGenerator = {
                generateItemForChallenge(it)
            },
            onClick = { clickEvent, challenge ->
                val player = clickEvent.player
                if (Timer.running) {
                    player.sendMessage(StckUtilsPlugin.prefix + "§cThe Timer has to be paused to do this")
                }
                clickEvent.bukkitEvent.isCancelled = true
                if (clickEvent.bukkitEvent.isLeftClick) {
                    challenge.active = !challenge.active
                    clickEvent.bukkitEvent.clickedInventory!!.setItem(clickEvent.bukkitEvent.slot, generateItemForChallenge(challenge))
                } else if (clickEvent.bukkitEvent.isRightClick) {
                    val configGUI = challenge.configurationGUI()
                    if (configGUI != null) {
                        if (challenge.active) {
                            player.openGUI(configGUI)
                        } else {
                            player.sendMessage(StckUtilsPlugin.prefix + "§cYou need to activate the Challenge before you can configure it")
                        }
                    }
                }
            }
        )

        compound.addContent(ChallengeManager.challenges.keys)
    }

    // GameChange Page
    page(3) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY
        this.transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY

        // Placeholders at the left Border
        placeholder(Slots.Border, placeHolderItemGray)

        // Go back Item
        pageChanger(Slots.RowThreeSlotOne, goBackItem, 1, null, null)

        // Compound for displaying the GameChanges
        val compound = createRectCompound<GameChange>(
            Slots.RowOneSlotTwo, Slots.RowFiveSlotNine,
            iconGenerator = {
                generateItemForChange(it)
            },
            onClick = { clickEvent, change ->
                val player = clickEvent.player
                clickEvent.bukkitEvent.isCancelled = true
                if (clickEvent.bukkitEvent.isLeftClick) {
                    change.active = !change.active
                    change.run()
                    clickEvent.bukkitEvent.clickedInventory!!.setItem(clickEvent.bukkitEvent.slot, generateItemForChange(change))
                } else if (clickEvent.bukkitEvent.isRightClick) {
                    val configGUI = change.configurationGUI()
                    if (configGUI != null) {
                        if (change.active) {
                            player.openGUI(configGUI)
                        } else {
                            player.sendMessage(StckUtilsPlugin.prefix + "§cYou need to activate the Game change before you can configure it")
                        }
                    }
                }
            }
        )

        compound.addContent(GameChangeManager.gameChanges.keys)
    }

    // Goals Page
    page(4) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY
        this.transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY

        // Placeholder at the left Border
        placeholder(Slots.Border, placeHolderItemGray)

        // Go back Item
        pageChanger(Slots.RowThreeSlotOne, goBackItem, 1, null, null)

        // Compound for the Goals
        val compound = createRectCompound<Goal>(
            Slots.RowOneSlotTwo, Slots.RowFiveSlotNine,
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

        compound.addContent(GoalManager.goals)
    }

    // Settings Page for the Timer
    page(-1) {

        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_VERTICALLY
        this.transitionFrom = PageChangeEffect.SLIDE_VERTICALLY

        // Placeholders
        placeholder(Slots.Border, placeHolderItemGray)
        placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

        // go back Item
        pageChanger(Slots.RowFiveSlotFive, goBackItem, 0, null, null)

        // If the Timer is not running display a Item for Starting the Timer
        if (!Timer.running) {
            button(Slots.RowThreeSlotThree, itemStack(Material.GREEN_DYE) {
                meta {
                    name = "§aStart the Timer"
                    addLore {
                        + " "
                        + "§7Click to Start the Timer"
                    }
                }
            }) {
                Timer.start()
                Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §astarted"))
                it.player.closeInventory()
            }
        // If Timer is Running display Item for stopping the Timer
        } else {
            button(Slots.RowThreeSlotThree, itemStack(Material.REDSTONE) {
                meta {
                    name = "§6Stop the Timer"
                    addLore {
                        + " "
                        + "§7Click to Stop the Timer"
                    }
                }
            }) {
                Timer.stop()
                Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §6stopped"))
                it.player.closeInventory()
            }
        }

        // Item for changing the Time
        button(Slots.RowThreeSlotFive, generateTimerItem()) {
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

        // Item for resetting the Timer
        button(Slots.RowThreeSlotSeven, itemStack(Material.BARRIER) {
            meta {
                name = "§cReset the Timer"
                addLore {
                    + " "
                    + "§7Click to Reset the Timer"
                }
            }
        }) {
            if (!Timer.running) {
                Timer.reset()
                Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §creset"))
                it.player.closeInventory()
            } else {
                it.player.sendMessage(StckUtilsPlugin.prefix + "§cYou can't reset the Timer while it's running")
            }
        }
    }

    // Settings Page for World reset
    page(-2) {
        // Transitions
        this.transitionTo = PageChangeEffect.SLIDE_VERTICALLY
        this.transitionFrom = PageChangeEffect.SLIDE_VERTICALLY

        // Placeholders
        placeholder(Slots.Border, placeHolderItemGray)
        placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

        // Go back Item
        pageChanger(Slots.RowFiveSlotFive, goBackItem, 0, null, null)

        // Item for activating/deactivating Village Spawn
        button(Slots.RowThreeSlotThree, generateVillageSpawnItem()) {
            Config.resetSettings.villageSpawn = !Config.resetSettings.villageSpawn
            it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateVillageSpawnItem())
        }

        // Item for running a World reset
        button(Slots.RowThreeSlotSeven, itemStack(Material.BARRIER) {
            meta {
                name = "§cReset World"
                addLore {
                    + " "
                    + "§7Click to §creset §7the World"
                    + "§7All Progress will be gone and"
                    + "§7the Timer will start at 0"
                }
            }
        }) {
            it.player.resetWorlds()
        }
    }
}