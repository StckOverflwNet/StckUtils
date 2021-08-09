package de.stckoverflw.stckutils.minecraft.gamechange.impl

import de.stckoverflw.stckutils.minecraft.gamechange.GameChange
import de.stckoverflw.stckutils.user.goBackItem
import de.stckoverflw.stckutils.user.placeHolderItemGray
import de.stckoverflw.stckutils.user.placeHolderItemWhite
import de.stckoverflw.stckutils.user.settingsGUI
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

object DamageMultiplier : GameChange() {
    override val id: String = "damage-multiplier"
    override val name: String = "§6DamageMultiplier"
    override val description: List<String> = listOf(
        " ",
        "§7Multiplies the damage you do to entities"
    )
    override val material: Material = Material.IRON_SWORD
    override val usesEvents: Boolean = true
    override val defaultActivated: Boolean = false

    override fun configurationGUI(): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = name
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, goBackItem) { it.player.openGUI(settingsGUI(), 3) }

            button(Slots.RowThreeSlotSix, plusItem()) {
                it.bukkitEvent.isCancelled = true
                handleUpdateClick(it.bukkitEvent, true)
                updateInventory(it.bukkitEvent.inventory)
            }

            button(Slots.RowThreeSlotFive, resetItem()) {
                it.bukkitEvent.isCancelled = true
                multiplier = 1.0
                updateInventory(it.bukkitEvent.inventory)
            }

            button(Slots.RowThreeSlotFour, minusItem()) {
                it.bukkitEvent.isCancelled = true
                handleUpdateClick(it.bukkitEvent, false)
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
                +"§7Reset the value of the Multiplier"
                +"§7Value: §f$multiplier §7(§8Default: 1.0§7)"
            }
        }
    }

    private fun plusItem() = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = "§aPlus"
            addLore {
                +" "
                +"§7Increase the value of the Multiplier"
                +"§7Value: §f$multiplier"
                +" "
                +"§7Left-click + Shift:     §a+ 0.1"
                +"§7Left-click:             §a+ 0.5"
                +"§7Right-click + Shift:    §a+ 1"
                +"§7Right-click:            §a+ 10"
            }
        }
    }

    private fun minusItem() = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = "§cMinus"
            addLore {
                +" "
                +"§7Decrease the value of the Multiplier"
                +"§7Value: §f$multiplier"
                +" "
                +"§7Left-click + Shift:     §c- 0.1"
                +"§7Left-click:             §c- 0.5"
                +"§7Right-click + Shift:    §c- 1"
                +"§7Right-click:            §c- 10"
            }
        }
    }

    private fun handleUpdateClick(event: InventoryClickEvent, isPositive: Boolean) {
        if (event.isLeftClick) {
            if (event.isShiftClick) {
                updateMultiplier(if (isPositive) 0.1 else -0.1)
                return
            } else {
                updateMultiplier(if (isPositive) 0.5 else -0.5)
                return
            }
        } else if (event.isRightClick) {
            if (event.isShiftClick) {
                updateMultiplier(if (isPositive) 1.0 else -1.0)
                return
            } else {
                updateMultiplier(if (isPositive) 10.0 else -10.0)
                return
            }
        }
    }

    private fun updateMultiplier(value: Double) {
        if ((multiplier + value) < maxMultiplier && (multiplier + value) > minMultiplier) {
            multiplier += value
        } else {
            multiplier = if (value > 0) maxMultiplier else minMultiplier
        }
    }

    override fun run() {
    }

    private var minMultiplier: Double = 0.1
    private var maxMultiplier: Double = 100.0
    private var multiplier: Double = 1.0

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.damager is Player) {
            event.damage *= multiplier
        }
    }
}