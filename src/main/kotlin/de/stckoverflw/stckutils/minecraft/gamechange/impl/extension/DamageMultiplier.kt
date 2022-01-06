package de.stckoverflw.stckutils.minecraft.gamechange.impl.extension

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.minecraft.challenge.descriptionKey
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
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

    private var minMultiplier: Double = 0.1
    private var maxMultiplier: Double = 100.0
    private var multiplier: Double
        get() = (Config.gameChangeConfig.getSetting(id, "multiplier") ?: 1.0) as Double
        set(value) = Config.gameChangeConfig.setSetting(id, "multiplier", value)

    override val id: String = "damage-multiplier"
    override val usesEvents: Boolean = true

    override fun item(locale: Locale) = itemStack(Material.IRON_SWORD) {
        meta {
            name = GameChangeManager.translationsProvider.translate(
                nameKey,
                locale,
                id
            )
            addLore {
                GameChangeManager.translationsProvider.translate(
                    descriptionKey,
                    locale,
                    id,
                    arrayOf(
                        if (active) {
                            "§a" + StckUtilsPlugin.translationsProvider.translate(
                                "generic.activated",
                                locale,
                                "general"
                            )
                        } else {
                            "§c" + StckUtilsPlugin.translationsProvider.translate(
                                "generic.disabled",
                                locale,
                                "general"
                            )
                        }
                    )
                )
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

        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item(event.player.language))
    }

    fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = GameChangeManager.translationsProvider.translate(
            nameKey,
            locale,
            id
        )
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, getGoBackItem(locale)) { it.player.openGUI(settingsGUI(locale), 3) }

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
            name = GameChangeManager.translationsProvider.translate(
                "reset_item.name",
                locale,
                id
            )
            addLore {
                GameChangeManager.translationsProvider.translate(
                    "reset_item.lore",
                    locale,
                    id,
                    arrayOf(String.format("%.1f", multiplier))
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }

    private fun plusItem(locale: Locale) = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = GameChangeManager.translationsProvider.translate(
                "plus_item.name",
                locale,
                id
            )
            addLore {
                GameChangeManager.translationsProvider.translate(
                    "plus_item.lore",
                    locale,
                    id,
                    arrayOf(String.format("%.1f", multiplier))
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }

    private fun minusItem(locale: Locale) = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = GameChangeManager.translationsProvider.translate(
                "minus_item.name",
                locale,
                id
            )
            addLore {
                GameChangeManager.translationsProvider.translate(
                    "minus_item.lore",
                    locale,
                    id,
                    arrayOf(String.format("%.1f", multiplier))
                ).split("\n").forEach {
                    +it
                }
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

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!active) return
        if (event.damager is Player) {
            event.damage *= multiplier
        }
    }
}
