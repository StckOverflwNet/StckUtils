package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.user.goBackItem
import de.stckoverflw.stckutils.user.placeHolderItemGray
import de.stckoverflw.stckutils.user.placeHolderItemWhite
import de.stckoverflw.stckutils.user.settingsGUI
import net.axay.kspigot.extensions.bukkit.give
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object RandomItem : Challenge() {
    override val id: String = "random-item"
    override val name: String = "§eRandom Item"
    override val material: Material = Material.BEACON
    override val description: List<String> = listOf(
        " ",
        "§7You get a random item every x Blocks and/or x Minutes",
        "§7(combines well with ≫No Block Break≪, ≫No Crafting≪, etc.)",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = name
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, goBackItem) { it.player.openGUI(settingsGUI(), 1) }

            button(Slots.RowThreeSlotFour, distanceItem()) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    isDistance = !isDistance
                    it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, distanceItem())
                } else if (it.bukkitEvent.isRightClick) {
                    it.guiInstance.gotoPage(1)
                }
            }

            button(Slots.RowThreeSlotSix, timeItem()) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    isTime = !isTime
                    it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, timeItem())
                } else if (it.bukkitEvent.isRightClick) {
                    it.guiInstance.gotoPage(2)
                }
            }
        }
        // distance settings
        page(1) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            pageChanger(Slots.RowThreeSlotOne, goBackItem, 0, null, null)

            button(Slots.RowThreeSlotSix, plusItem("distance", "${distanceUnit}§7m", "10§7m", "100§7m")) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    distanceUnit += 10
                } else if (it.bukkitEvent.isRightClick) {
                    distanceUnit += 100
                }
                updateInventory(it.bukkitEvent.inventory, false)
            }

            button(Slots.RowThreeSlotFive, resetItem("distance", "${distanceUnit}§7m", "500§7m")) {
                it.bukkitEvent.isCancelled = true
                distanceUnit = 500
                updateInventory(it.bukkitEvent.inventory, false)
            }

            button(Slots.RowThreeSlotFour, minusItem("distance", "${distanceUnit}§7m", "10§7m", "100§7m")) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    if (distanceUnit - 10 < minDistance)
                        distanceUnit = minDistance
                    else
                        distanceUnit -= 10
                } else if (it.bukkitEvent.isRightClick) {
                    if (distanceUnit - 100 < minDistance)
                        distanceUnit = minDistance
                    else
                        distanceUnit -= 100
                }
                updateInventory(it.bukkitEvent.inventory, false)
            }
        }
        // time settings
        page(2) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            pageChanger(Slots.RowThreeSlotOne, goBackItem, 0, null, null)

            button(Slots.RowThreeSlotSix, plusItem("time", Timer.formatTime(timeUnit.toLong()), "10§7s", "1§7m")) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    timeUnit += 10
                } else if (it.bukkitEvent.isRightClick) {
                    timeUnit += 60
                }
                updateInventory(it.bukkitEvent.inventory, true)
            }

            button(Slots.RowThreeSlotFive, resetItem("time", Timer.formatTime(timeUnit.toLong()), "3§7m")) {
                it.bukkitEvent.isCancelled = true
                timeUnit = 300
                updateInventory(it.bukkitEvent.inventory, true)
            }

            button(Slots.RowThreeSlotFour, minusItem("time", Timer.formatTime(timeUnit.toLong()), "10§7s", "1§7m")) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    if (timeUnit - 10 < minTime)
                        timeUnit = minTime
                    else
                        timeUnit -= 10
                } else if (it.bukkitEvent.isRightClick) {
                    if (timeUnit - 60 < minTime)
                        timeUnit = minTime
                    else
                        timeUnit -= 60
                }
                updateInventory(it.bukkitEvent.inventory, true)
            }
        }
    }

    private fun updateInventory(inv: Inventory, isTime: Boolean) {
        if (isTime) {
            inv.setItem(21, minusItem("time", Timer.formatTime(timeUnit.toLong()), "10§7s", "1§7m"))
            inv.setItem(22, resetItem("time", Timer.formatTime(timeUnit.toLong()), "3§7m"))
            inv.setItem(23, plusItem("time", Timer.formatTime(timeUnit.toLong()), "10§7s", "1§7m"))
        } else {
            inv.setItem(21, minusItem("distance", "${distanceUnit}§7m", "10§7m", "100§7m"))
            inv.setItem(22, resetItem("distance", "${distanceUnit}§7m", "500§7m"))
            inv.setItem(23, plusItem("distance", "${distanceUnit}§7m", "10§7m", "100§7m"))
        }
    }

    private fun resetItem(description: String, value: String, default: String) = itemStack(Material.BARRIER) {
        meta {
            name = "§4Reset"
            addLore {
                +" "
                +"§7Reset the $description"
                +"§7$description: §f$value (§8Default: $default)"
            }
        }
    }

    private fun plusItem(description: String, value: String, leftClick: String, rightClick: String) = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = "§aPlus"
            addLore {
                +" "
                +"§7Increase the $description"
                +"§7$description: §f$value"
                +" "
                +"§7Left-click:             §a+ $leftClick"
                +"§7Right-click:            §a+ $rightClick"
            }
        }
    }

    private fun minusItem(description: String, value: String, leftClick: String, rightClick: String) =
        itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
            meta {
                name = "§cMinus"
                addLore {
                    +" "
                    +"§7Decrease the $description"
                    +"§7$description: §f$value"
                    +" "
                    +"§7Left-click:             §c- $leftClick"
                    +"§7Right-click:            §c- $rightClick"
                }
            }
        }

    private fun distanceItem() = itemStack(Material.GOLDEN_BOOTS) {
        meta {
            name = "§aDistance"
            addLore {
                +" "
                +"§7Toggle if random items are dropped for distance travelled"
                +"§7Currently ".plus(if (isDistance) "§aenabled §7(${distanceUnit}m)" else "§cdisabled")
                +"§7Right Click to open more Settings"
            }
        }
    }

    private fun timeItem() = itemStack(Material.CLOCK) {
        meta {
            name = "§aBreakable Lines"
            addLore {
                +" "
                +"§7Toggle if random items are dropped for time passed"
                +"§7Currently ".plus(if (isTime) "§aenabled §7(${timeUnit}s)" else "§cdisabled")
                +"§7Right Click to open more Settings"
            }
        }
    }

    private var isDistance
        get() = (Config.gameChangeConfig.getSetting(id, "isDistance") ?: true) as Boolean
        set(value) = Config.gameChangeConfig.setSetting(id, "isDistance", value)
    private var distanceUnit
        get() = (Config.gameChangeConfig.getSetting(id, "distanceUnit") ?: 500) as Int
        set(value) = Config.gameChangeConfig.setSetting(id, "distanceUnit", value)
    private var distance
        get() = (Config.gameChangeConfig.getSetting(id, "distance") ?: 0) as Int
        set(value) = Config.gameChangeConfig.setSetting(id, "distance", value)
    private var minDistance: Int = 50

    private var isTime
        get() = (Config.gameChangeConfig.getSetting(id, "isTime") ?: true) as Boolean
        set(value) = Config.gameChangeConfig.setSetting(id, "isTime", value)
    private var timeUnit
        get() = (Config.gameChangeConfig.getSetting(id, "timeUnit") ?: 300) as Int
        set(value) = Config.gameChangeConfig.setSetting(id, "timeUnit", value)
    private var time
        get() = (Config.gameChangeConfig.getSetting(id, "time") ?: 0) as Int
        set(value) = Config.gameChangeConfig.setSetting(id, "time", value)
    private var minTime: Int = 10

    private val materials = Material.values().filter { material -> material.isItem }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!isDistance) return
        if (!event.hasChangedBlock()) return
        distance++
        if (distance >= distanceUnit) {
            distance = 0
            onlinePlayers.forEach { player ->
                if (player.gameMode == GameMode.SURVIVAL)
                    player.give(ItemStack(materials.random())).values.forEach { itemStack -> player.world.dropItem(player.location, itemStack) }
            }
        }
    }

    override fun update() {
        if (!isTime) return
        time++
        if (time >= timeUnit) {
            time = 0
            onlinePlayers.forEach { player ->
                if (player.gameMode == GameMode.SURVIVAL)
                    player.give(ItemStack(materials.random())).values.forEach { itemStack -> player.world.dropItem(player.location, itemStack) }
            }
        }
    }
}
