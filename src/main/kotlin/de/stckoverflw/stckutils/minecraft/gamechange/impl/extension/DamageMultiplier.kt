package de.stckoverflw.stckutils.minecraft.gamechange.impl.extension

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.minecraft.gamechange.GameExtension
import de.stckoverflw.stckutils.minecraft.gamechange.active
import de.stckoverflw.stckutils.minecraft.gamechange.descriptionKey
import de.stckoverflw.stckutils.minecraft.gamechange.nameKey
import de.stckoverflw.stckutils.util.Colors
import de.stckoverflw.stckutils.util.GUIPage
import de.stckoverflw.stckutils.util.getGoBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.extensions.bukkit.render
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.gui.rectTo
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.flags
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import java.util.Locale
import kotlin.math.roundToInt

object DamageMultiplier : GameExtension() {

    private var minMultiplier: Double = 0.1
    private var maxMultiplier: Double = 100.0
    private var multiplier: Double
        get() = (Config.gameChangeConfig.getSetting(id, "multiplier") ?: 1.0) as Double
        set(value) = Config.gameChangeConfig.setSetting(id, "multiplier", value)

    override val id: String = "damage-multiplier"
    override val usesEvents: Boolean = true

    override fun item(locale: Locale) = itemStack(Material.IRON_SWORD) {
        meta {
            name = translatable(nameKey)
                .color(Colors.GOAL_COMPOUND)
                .render(locale)

            addLore {
                addComponent(
                    translatable(descriptionKey)
                        .args(
                            if (active) {
                                translatable("generic.activated")
                                    .color(Colors.ACTIVE)
                            } else {
                                translatable("generic.disabled")
                                    .color(Colors.INACTIVE)
                            }
                        )
                        .color(Colors.GOAL_COMPOUND_SECONDARY)
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

    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        event.bukkitEvent.isCancelled = true
        if (event.bukkitEvent.isLeftClick) {
            active = !active
        } else if (event.bukkitEvent.isRightClick) {
            event.player.openGUI(configurationGUI(event.player.locale()))
        }
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item(event.player.locale()))
    }

    fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = translatable(nameKey)
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, getGoBackItem(locale)) { it.player.openGUI(settingsGUI(locale), GUIPage.gameChangesPage) }

            button(Slots.RowThreeSlotSix, plusItem(locale)) {
                it.bukkitEvent.isCancelled = true
                handleUpdateClick(it.bukkitEvent, true)
                updateInventory(it.bukkitEvent.inventory, locale)
            }

            button(Slots.RowThreeSlotFive, resetItem(locale)) {
                it.bukkitEvent.isCancelled = true
                multiplier = 1.0
                updateInventory(it.bukkitEvent.inventory, locale)
            }

            button(Slots.RowThreeSlotFour, minusItem(locale)) {
                it.bukkitEvent.isCancelled = true
                handleUpdateClick(it.bukkitEvent, false)
                updateInventory(it.bukkitEvent.inventory, locale)
            }
        }
    }

    private fun updateInventory(inv: Inventory, locale: Locale) {
        inv.setItem(21, minusItem(locale))
        inv.setItem(22, resetItem(locale))
        inv.setItem(23, plusItem(locale))
    }

    private fun resetItem(locale: Locale) = itemStack(Material.BARRIER) {
        meta {
            name = translatable("$id.reset_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.reset_item.lore")
                        .args(text(String.format("%.1f", multiplier)))
                        .render(locale)
                )
            }
        }
    }

    private fun plusItem(locale: Locale) = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = translatable("$id.plus_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.plus_item.lore")
                        .args(text(String.format("%.1f", multiplier)))
                        .render(locale)
                )
            }
        }
    }

    private fun minusItem(locale: Locale) = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = translatable("$id.minus_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.minus_item.lore")
                        .args(text(String.format("%.1f", multiplier)))
                        .render(locale)
                )
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
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!active) return
        if (event.damager is Player) {
            event.damage *= multiplier
        }
    }
}
