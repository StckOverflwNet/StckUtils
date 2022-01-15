package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.coloredString
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.saveInventory
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.GUIPage
import de.stckoverflw.stckutils.util.getGoBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.gui.rectTo
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.Locale

object InventorySwap : Challenge() {

    private var minPeriod: Int = 1
    private var period: Int = 60
    private var time: Int
        get() = Config.challengeDataConfig.getSetting(id, "time") as Int? ?: 0
        set(value) = Config.challengeDataConfig.setSetting(id, "time", value)

    override val id: String = "inventory-swap"
    override val material: Material = Material.CHEST_MINECART
    override val usesEvents: Boolean = false

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = translatable(nameKey).coloredString(locale)
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, getGoBackItem(locale)) { it.player.openGUI(settingsGUI(locale), GUIPage.challengesPageNumber) }

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
            name = translatable("$id.reset_item.name")
            addLore {
                addComponent(
                    translatable(
                        "$id.reset_item.lore",
                        listOf(
                            text(ChatColor.stripColor(Timer.formatTime(period.toLong())) ?: "n/a")
                        )
                    )
                )
            }
        }
    }

    private fun plusItem() = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = translatable("$id.plus_item.name")
            addLore {
                addComponent(
                    translatable(
                        "$id.plus_item.lore",
                        listOf(text(ChatColor.stripColor(Timer.formatTime(period.toLong())) ?: "n/a"))
                    )
                )
            }
        }
    }

    private fun minusItem() = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = translatable("$id.minus_item.name")
            addLore {
                addComponent(
                    translatable(
                        "$id.minus_item.lore",
                        listOf(text(ChatColor.stripColor(Timer.formatTime(period.toLong())) ?: "n/a"))
                    )
                )
            }
        }
    }

    override fun update() {
        time++
        if (time % period != 0) return
        time = 0

        val players = onlinePlayers.filter { it.isPlaying() }
        if (players.size <= 1) {
            return
        }

        val inventories: Map<Player, Array<ItemStack?>> =
            players.zip(players.map { player -> player.inventory.contents as Array<ItemStack?> }.shuffled()).toMap()

        players.forEach { player ->
            if (!player.inventory.contents.contentEquals(inventories[player])) {
                player.inventory.clear()
                @Suppress("UNCHECKED_CAST")
                player.inventory.setContents(inventories[player]!! as Array<out ItemStack>)
                player.saveInventory()
            }
        }
    }
}
