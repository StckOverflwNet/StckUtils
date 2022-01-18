package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.coloredString
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
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
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.runnables.sync
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import java.util.Locale

object ChunkFlattener : Challenge() {

    private var minPeriod: Int = 1
    private var maxPeriod: Int = 60
    private var period: Int
        get() = Config.challengeConfig.getSetting(id, "period") as Int? ?: 10
        set(value) = Config.challengeConfig.setSetting(id, "period", value)
    private var doDrop: Boolean
        get() = Config.challengeConfig.getSetting(id, "doDrop") as Boolean? ?: false
        set(value) = Config.challengeConfig.setSetting(id, "doDrop", value)
    private var time: Int
        get() = Config.challengeDataConfig.getSetting(id, "time") as Int? ?: 0
        set(value) = Config.challengeDataConfig.setSetting(id, "time", value)

    override val id: String = "chunk-flattener"
    override val material: Material = Material.BEDROCK
    override val usesEvents: Boolean = false

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = translatable(nameKey).coloredString(locale)
        defaultPage = 0

        page(0) {
            placeholder(Slots.Border, placeHolderItemGray)
            placeholder(Slots.BorderPaddingOne, placeHolderItemWhite)

            button(
                Slots.RowThreeSlotOne,
                getGoBackItem(locale)
            ) {
                it.player.openGUI(settingsGUI(locale), GUIPage.challengesPageNumber)
            }

            button(
                Slots.RowThreeSlotSeven,
                plusItem(locale)
            ) {
                it.bukkitEvent.isCancelled = true
                handleUpdateClick(it.bukkitEvent, true)
                updateInventory(it.bukkitEvent.inventory, locale)
            }

            button(
                Slots.RowThreeSlotSix,
                resetItem(locale)
            ) {
                it.bukkitEvent.isCancelled = true
                period = 10
                updateInventory(it.bukkitEvent.inventory, locale)
            }

            button(
                Slots.RowThreeSlotFive,
                minusItem(locale)
            ) {
                it.bukkitEvent.isCancelled = true
                handleUpdateClick(it.bukkitEvent, false)
                updateInventory(it.bukkitEvent.inventory, locale)
            }

            button(
                Slots.RowThreeSlotThree,
                dropItem(locale)
            ) {
                it.bukkitEvent.isCancelled = true
                doDrop = !doDrop
                updateInventory(it.bukkitEvent.inventory, locale)
            }
        }
    }

    private fun updateInventory(inv: Inventory, locale: Locale) {
        inv.setItem(20, dropItem(locale))
        inv.setItem(22, minusItem(locale))
        inv.setItem(23, resetItem(locale))
        inv.setItem(24, plusItem(locale))
    }

    private fun dropItem(locale: Locale) = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = translatable("$id.drop_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.drop_item.lore")
                        .args(
                            if (doDrop) {
                                translatable("generic.activated", TextColor.color(Color.GREEN.asRGB()))
                            } else {
                                translatable("generic.activated", TextColor.color(Color.RED.asRGB()))
                            }
                        )
                        .render(locale)
                )
            }
        }
    }

    private fun resetItem(locale: Locale) = itemStack(Material.BARRIER) {
        meta {
            name = translatable("$id.reset_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.reset_item.lore")
                        .args(text(period))
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
                        .args(text(period))
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
                        .args(text(period))
                        .render(locale)
                )
            }
        }
    }

    private fun handleUpdateClick(event: InventoryClickEvent, isPositive: Boolean) {
        if (event.isLeftClick) {
            updateMultiplier(if (isPositive) 1 else -1)
            return
        } else if (event.isRightClick) {
            updateMultiplier(if (isPositive) 10 else -10)
            return
        }
    }

    private fun updateMultiplier(value: Int) {
        if ((period + value) < maxPeriod && (period + value) > minPeriod) {
            period += value
        } else {
            period = if (value > 0) maxPeriod else minPeriod
        }
    }

    override fun update() {
        time++
        if (time % period != 0) return
        time = 0

        onlinePlayers.forEach { player ->
            if (!player.isPlaying()) return@forEach
            for (i in 0..15) {
                for (j in 0..15) {
                    sync {
                        val block = player.world.getHighestBlockAt(player.location.chunk.x * 16 + i, player.location.chunk.z * 16 + j)
                        if (block.type.blastResistance > 1200.0F) return@sync
                        if (doDrop) {
                            block.breakNaturally()
                        } else {
                            val blocks = ArrayList<Block>()
                            blocks.add(block)
                            while (blocks[blocks.lastIndex].getRelative(BlockFace.UP).type != Material.AIR) {
                                blocks.add(blocks[blocks.lastIndex].getRelative(BlockFace.UP))
                            }
                            blocks.reverse()
                            blocks.forEach {
                                player.world.playEffect(it.location, Effect.STEP_SOUND, it.type)
                                player.world.playSound(it.location, it.soundGroup.breakSound, 0.5F, 1F)
                                it.type = Material.AIR
                            }
                        }
                    }
                }
            }
        }
    }
}
