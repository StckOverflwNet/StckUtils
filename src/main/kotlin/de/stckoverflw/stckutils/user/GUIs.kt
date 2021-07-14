package de.stckoverflw.stckutils.user

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.challenge.Challenge
import de.stckoverflw.stckutils.challenge.ChallengeManager
import de.stckoverflw.stckutils.challenge.active
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.resetWorlds
import de.stckoverflw.stckutils.gamechange.GameChange
import de.stckoverflw.stckutils.gamechange.GameChangeManager
import de.stckoverflw.stckutils.gamechange.active
import de.stckoverflw.stckutils.goal.Goal
import de.stckoverflw.stckutils.goal.GoalManager
import de.stckoverflw.stckutils.timer.Timer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * The Method to generate a new Instance of the Settings GUI
 */
fun settingsGUI(): GUI<ForInventoryThreeByNine> = kSpigotGUI(GUIType.THREE_BY_NINE) {
    title = "§9Settings"
    defaultPage = 0
    page(0) {
        placeholder(Slots.RowOneSlotOne rectTo Slots.RowThreeSlotNine, placeHolderItem)
        button(Slots.RowTwoSlotTwo, itemStack(Material.DRAGON_HEAD) {
            meta {
                name = "§6Challenges"
                addLore {
                    + "§6Challenges§7, change the Minecraft Gameplay"
                    + " "
                    + "§7Click to open the Challenge Inventory"
                }
            }
        }) {
            it.player.openGUI(challengesGUI())
        }
        button(Slots.RowTwoSlotThree, itemStack(Material.FILLED_MAP) {
            meta {
                name = "§cGame Changes"
                addLore {
                    + "§cGame Changes§7, change basic Game mechanics or"
                    + "§7add features to the basic Game"
                    + " "
                    + "§7Click to open the Challenge Inventory"
                }
            }
        }) {
            it.player.openGUI(changesGUI())
        }

        button(Slots.RowTwoSlotSix, itemStack(Material.GRASS_BLOCK) {
            meta {
                name = "${KColors.ROSYBROWN}World Reset"
                addLore {
                    + "${KColors.ROSYBROWN}World Reset§7, reset the world"
                    + "§7in game"
                    + " "
                    + "§7Click to open the Challenge Inventory"
                }
            }
        }) {
            it.player.openGUI(resetGUI())
        }
        button(Slots.RowTwoSlotEight, itemStack(Material.CLOCK) {
            meta {
                name = "§eTimer"
                addLore {
                    + "§eTimer§7, a simple Timer that counts"
                    + "§7upwards in seconds, minutes and hours"
                    + " "
                    + "§7Click to open the Timer Inventory"
                }
            }
        }) {
            it.player.openGUI(timerGUI(true))
        }
        button(Slots.RowTwoSlotSeven, itemStack(Material.DIAMOND) {
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
        }) {
            it.player.openGUI(goalsGUI())
        }
    }
}

/**
 * The Method to generate a new Instance of the Challenges GUI
 */
fun challengesGUI() = kSpigotGUI(GUIType.FOUR_BY_NINE) {
    title = "§6Challenges"
    page(1) {
        placeholder(Slots.BorderPaddingThree, placeHolderItem)
        button(Slots.RowOneSlotOne, goBackItem) { it.player.openGUI(settingsGUI()) }
        val compound = createRectCompound<ItemStack>(
            Slots.RowTwoSlotTwo, Slots.RowThreeSlotEight,
            iconGenerator = {
                it
            },
            onClick = { clickEvent, element ->
                val player = clickEvent.player
                val challenge = ChallengeManager.getChallenge(element.itemMeta.localName)
                if (challenge != null) {
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
            }
        )

        val items = ArrayList<ItemStack>()

        ChallengeManager.challenges.forEach { (challenge, _) ->
            items.add(generateItemForChallenge(challenge))
        }

        compound.addContent(items)
    }
}

/**
 * The Method to generate a new Instance of the Game Changes GUI
 */
fun changesGUI() = kSpigotGUI(GUIType.FOUR_BY_NINE) {
    title = "§cGame Changes"
    page(1) {
        placeholder(Slots.BorderPaddingThree, placeHolderItem)
        button(Slots.RowOneSlotOne, goBackItem) { it.player.openGUI(settingsGUI()) }

        val compound = createRectCompound<ItemStack>(
            Slots.RowTwoSlotTwo, Slots.RowThreeSlotEight,
            iconGenerator = {
                it
            },
            onClick = { clickEvent, element ->
                val player = clickEvent.player
                val change = GameChangeManager.getGameChange(element.itemMeta.localName)
                if (change != null) {
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
            }
        )

        val items = ArrayList<ItemStack>()

        GameChangeManager.gameChanges.forEach { (change, _) ->
            items.add(generateItemForChange(change))
        }

        compound.addContent(items)
    }
}

/**
 * The Method to generate a new Instance of the Goals GUI
 */
fun goalsGUI(): GUI<ForInventoryThreeByNine> = kSpigotGUI(GUIType.THREE_BY_NINE) {
    title = "§bGoals"
    page(1) {
        placeholder(Slots.BorderPaddingThree, placeHolderItem)
        button(Slots.RowOneSlotOne, goBackItem) { it.player.openGUI(settingsGUI()) }

        val compound = createRectCompound<ItemStack>(
            Slots.RowTwoSlotTwo, Slots.RowTwoSlotEight,
            iconGenerator = {
                it
            },
            onClick = { clickEvent, element ->
                val goal = GoalManager.getGoal(element.itemMeta.localName)
                if (goal != null) {
                    clickEvent.bukkitEvent.isCancelled = true
                    GoalManager.activeGoal = goal
                    clickEvent.player.openGUI(goalsGUI())
                }
            }
        )

        val items = ArrayList<ItemStack>()

        GoalManager.goals.forEach {
            items.add(generateItemForGoal(it))
        }

        compound.addContent(items)
    }
}

/**
 * The Method to generate a new Instance of the Timer GUI
 */
fun timerGUI(showGoBackItem: Boolean) = kSpigotGUI(GUIType.THREE_BY_NINE) {
    title = "§eTimer"
    page(1) {
        placeholder(Slots.RowOneSlotOne rectTo Slots.RowThreeSlotNine, placeHolderItem)
        if (showGoBackItem) {
            button(Slots.RowOneSlotOne, goBackItem) { it.player.openGUI(settingsGUI()) }
        }
        if (!Timer.running) {
            button(Slots.RowTwoSlotThree, itemStack(Material.GREEN_DYE) {
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
        } else {
            button(Slots.RowTwoSlotThree, itemStack(Material.REDSTONE) {
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

        button(Slots.RowTwoSlotFive, generateTimerItem()) {
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

        button(Slots.RowTwoSlotSeven, itemStack(Material.BARRIER) {
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
}

/**
 * The Method to generate a new Instance of the Timer GUI
 */
fun resetGUI() = kSpigotGUI(GUIType.THREE_BY_NINE) {
    title = "§cReset"
    page(1) {
        placeholder(Slots.RowOneSlotOne rectTo Slots.RowThreeSlotNine, placeHolderItem)
        button(Slots.RowOneSlotOne, goBackItem) { it.player.openGUI(settingsGUI()) }

        button(Slots.RowTwoSlotThree, generateVillageSpawnItem()) {
            Config.resetSettings.villageSpawn = !Config.resetSettings.villageSpawn
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateVillageSpawnItem())
        }

        button(Slots.RowTwoSlotSeven, itemStack(Material.BARRIER) {
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

private fun generateItemForChange(change: GameChange) = itemStack(change.material) {
        meta {
            name = change.name
            localName = change.id
            addLore {
                change.description.forEach {
                    + it
                }
                + " "
                if (change.active) {
                    + "§aActivated§7, Click to deactivate"
                } else {
                    + "§cDeactivated§7, Click to activate"
                }
                if (change.configurationGUI() != null) {
                    + "§7Right Click to open the Configuration for ${change.name}"
                }
            }
        }
     }
private fun generateItemForChallenge(challenge: Challenge) = itemStack(challenge.material) {
        meta {
            name = challenge.name
            localName = challenge.id
            addLore {
                challenge.description.forEach {
                    + it
                }
                + " "
                if (challenge.active) {
                    + "§aActivated§7, Click to deactivate"
                } else {
                    + "§cDeactivated§7, Click to activate"
                }
                if (challenge.configurationGUI() != null) {
                    + "§7Right Click to open the Configuration for ${challenge.name}"
                }
            }
        }
     }
private fun generateItemForGoal(goal: Goal) = itemStack(goal.material) {
    meta {
        name = goal.name
        localName = goal.id
        addLore {
            goal.description.forEach {
                + it
            }
            + " "
            if (GoalManager.activeGoal == goal) {
                + "§aThis Goal is currently activated"
            } else {
                +"§cThis Goal is currently deactivated,"
                +"§7click to activate it"
            }
        }
    }
}
private fun generateTimerItem() = itemStack(Material.CLOCK) {
    meta {
        name = "§6Change Timer Time"
        addLore {
            + " "
            if (Timer.time > 0) {
                + "§7Current Time: $Timer"
            } else {
                +"§7Current Time: §c0m"
            }
            + " "
            + "§7Left-click to higher §c1m"
            + "§7Right-click to lower §c1m"
        }
    }
}
private fun generateVillageSpawnItem() = itemStack(Material.VILLAGER_SPAWN_EGG) {
    meta {
        name = "${KColors.SANDYBROWN}Village Spawn"
        addLore {
            + " "
            if (Config.resetSettings.villageSpawn) {
                + "§7Currently §aactivated"
                + " "
                +"§7Click to §cdeactivate"
            } else {
                + "§7Currently §cdeactivated"
                + " "
                +"§7Click to §aactivate"
            }
        }
    }
}