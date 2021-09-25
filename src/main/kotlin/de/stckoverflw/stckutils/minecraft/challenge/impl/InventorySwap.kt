package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.saveInventory
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.goBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object InventorySwap : Challenge() {

    private var minPeriod: Int = 1
    private var period: Int = 60
    private var time: Int
        get() = Config.challengeDataConfig.getSetting(id, "time") as Int? ?: 0
        set(value) = Config.challengeDataConfig.setSetting(id, "time", value)

    override val id: String = "inventory-swap"
    override val name: String = "§2Inventory Swap"
    override val material: Material = Material.CHEST_MINECART
    override val description: List<String> = listOf(
        " ",
        "§7Swaps your inventory with",
        "§7someone elses every now and then",
    )
    override val usesEvents: Boolean = false

    override fun configurationGUI(): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = ChunkFlattener.name
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, goBackItem) { it.player.openGUI(settingsGUI(), 1) }

            button(Slots.RowThreeSlotFour, minusItem()) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    if (period - 1 < minPeriod)
                        period = minPeriod
                    else
                        period -= 1
                } else if (it.bukkitEvent.isRightClick) {
                    if (period - 10 < minPeriod)
                        period = minPeriod
                    else
                        period -= 10
                }
                updateInventory(it.bukkitEvent.inventory)
            }

            button(Slots.RowThreeSlotFive, resetItem()) {
                it.bukkitEvent.isCancelled = true
                period = 60
                updateInventory(it.bukkitEvent.inventory)
            }

            button(Slots.RowThreeSlotSix, plusItem()) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    period += 1
                } else if (it.bukkitEvent.isRightClick) {
                    period += 10
                }
                updateInventory(it.bukkitEvent.inventory)
            }
        }
    }

    private fun updateInventory(inv: Inventory) {
        inv.setItem(21, minusItem())
        inv.setItem(22, resetItem())
        inv.setItem(23, plusItem())
    }

    private fun resetItem() = itemStack(Material.BARRIER) {
        meta {
            name = "§4Reset"
            addLore {
                +" "
                +"§7Reset the period"
                +"§7Period: §f${ChatColor.stripColor(Timer.formatTime(period.toLong()))}§7 (§8Default: §71m)"
            }
        }
    }

    private fun plusItem() = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = "§aPlus"
            addLore {
                +" "
                +"§7Increase the period"
                +"§7Period: §f${ChatColor.stripColor(Timer.formatTime(period.toLong()))}§7"
                +" "
                +"§7Left-click:             §a+ 1§7s"
                +"§7Right-click:            §a+ 10§7s"
            }
        }
    }

    private fun minusItem() = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = "§cMinus"
            addLore {
                +" "
                +"§7Decrease the period"
                +"§7Period: §f${ChatColor.stripColor(Timer.formatTime(period.toLong()))}§7"
                +" "
                +"§7Left-click:             §c- 1§7s"
                +"§7Right-click:            §c- 10§7s"
            }
        }
    }

    override fun update() {
        time++
        if (time % period != 0) return
        time = 0

        val players = onlinePlayers.filter { it.isPlaying() }
        if (players.size <= 1) {
            players.forEach { it.sendMessage(StckUtilsPlugin.prefix + "§7You are alone, couldn't change inventory with anyone") }
            return
        }

        val inventories: Map<Player, Array<ItemStack?>> =
            players.zip(players.map { player -> player.inventory.contents }.shuffled()).toMap()

        players.forEach { player ->
            if (!player.inventory.contents.contentEquals(inventories[player])) {
                player.inventory.clear()
                player.inventory.contents = inventories[player]!!
                player.saveInventory()
            }
        }
    }
}
