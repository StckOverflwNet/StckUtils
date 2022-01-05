package de.stckoverflw.stckutils.minecraft.gamechange.impl.extension

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.minecraft.gamechange.GameExtension
import de.stckoverflw.stckutils.minecraft.gamechange.active
import de.stckoverflw.stckutils.util.getGoBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
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
import java.util.*
import kotlin.math.roundToInt

object DamageMultiplier : GameExtension() {
    override val id: String = "damage-multiplier"
    override val usesEvents: Boolean = true

    override fun item() = itemStack(Material.IRON_SWORD) {
        meta {
            name = "§6Damage Multiplier"
            addLore {
                +" "
                +"§7Multiplies the damage you do to entities"
                +" "
                +"§7Right-click: §fOpen more settings"
                +""
                +"§7Currently ".plus(if (active) "§aactivated" else "§cdeactivated")
            }
        }
    }

    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        event.bukkitEvent.isCancelled = true
        if (event.bukkitEvent.isLeftClick) {
            active = !active
        } else if (event.bukkitEvent.isRightClick) {
            event.player.openGUI(configurationGUI(event.player.language))
        }

        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item())
    }

    fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = "§6Damage Multiplier"
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, getGoBackItem(locale)) { it.player.openGUI(settingsGUI(locale), 3) }

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
                +"§7Value: §f${String.format("%.1f", multiplier)} §7(§8Default: 1.0§7)"
            }
        }
    }

    private fun plusItem() = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = "§aPlus"
            addLore {
                +" "
                +"§7Increase the value of the Multiplier"
                +"§7Value: §f${String.format("%.1f", multiplier)}"
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
                +"§7Value: §f${String.format("%.1f", multiplier)}"
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
        multiplier = if ((multiplier + value) < maxMultiplier && (multiplier + value) > minMultiplier) {
            ((multiplier + value) * 10.0).roundToInt() / 10.0
        } else {
            if (value > 0) maxMultiplier else minMultiplier
        }
    }

    override fun run() {
        // since this method is empty we don't ever call it
    }

    private var minMultiplier: Double = 0.1
    private var maxMultiplier: Double = 100.0
    private var multiplier: Double
        get() = (Config.gameChangeConfig.getSetting(id, "multiplier") ?: 1.0) as Double
        set(value) = Config.gameChangeConfig.setSetting(id, "multiplier", value)

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!active) return
        if (event.damager is Player) {
            event.damage *= multiplier
        }
    }
}
