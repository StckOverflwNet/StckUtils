package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.saveInventory
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.getGoBackItem
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
import java.util.*

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
        title = ChallengeManager.translationsProvider.translate(
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
            button(Slots.RowThreeSlotOne, getGoBackItem(locale)) { it.player.openGUI(settingsGUI(locale), 1) }

            button(Slots.RowThreeSlotFour, minusItem(locale)) {
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
                updateInventory(it.bukkitEvent.inventory, locale)
            }

            button(Slots.RowThreeSlotFive, resetItem(locale)) {
                it.bukkitEvent.isCancelled = true
                period = 60
                updateInventory(it.bukkitEvent.inventory, locale)
            }

            button(Slots.RowThreeSlotSix, plusItem(locale)) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    period += 1
                } else if (it.bukkitEvent.isRightClick) {
                    period += 10
                }
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
            name = ChallengeManager.translationsProvider.translate(
                "reset_item.name",
                locale,
                id
            )
            addLore {
                ChallengeManager.translationsProvider.translate(
                    "reset_item.lore",
                    locale,
                    id,
                    arrayOf(ChatColor.stripColor(Timer.formatTime(period.toLong())))
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }

    private fun plusItem(locale: Locale) = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = ChallengeManager.translationsProvider.translate(
                "plus_item.name",
                locale,
                id
            )
            addLore {
                ChallengeManager.translationsProvider.translate(
                    "plus_item.lore",
                    locale,
                    id,
                    arrayOf(ChatColor.stripColor(Timer.formatTime(period.toLong())))
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }

    private fun minusItem(locale: Locale) = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = ChallengeManager.translationsProvider.translate(
                "minus_item.name",
                locale,
                id
            )
            addLore {
                ChallengeManager.translationsProvider.translate(
                    "minus_item.lore",
                    locale,
                    id,
                    arrayOf(ChatColor.stripColor(Timer.formatTime(period.toLong())))
                ).split("\n").forEach {
                    +it
                }
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
