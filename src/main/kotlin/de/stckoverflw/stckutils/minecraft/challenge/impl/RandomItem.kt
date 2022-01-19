package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.coloredString
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.plainText
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.GUIPage
import de.stckoverflw.stckutils.util.getGoBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.extensions.bukkit.give
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.gotoPage
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.gui.rectTo
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.Locale

object RandomItem : Challenge() {

    private var isDistance
        get() = (Config.challengeConfig.getSetting(id, "isDistance") ?: true) as Boolean
        set(value) = Config.challengeConfig.setSetting(id, "isDistance", value)
    private var distanceUnit
        get() = (Config.challengeDataConfig.getSetting(id, "distanceUnit") ?: 500) as Int
        set(value) = Config.challengeDataConfig.setSetting(id, "distanceUnit", value)
    private var distance
        get() = (Config.challengeDataConfig.getSetting(id, "distance") ?: 0) as Int
        set(value) = Config.challengeDataConfig.setSetting(id, "distance", value)
    private var minDistance: Int = 50

    private var isTime
        get() = (Config.challengeConfig.getSetting(id, "isTime") ?: true) as Boolean
        set(value) = Config.challengeConfig.setSetting(id, "isTime", value)
    private var timeUnit
        get() = (Config.challengeDataConfig.getSetting(id, "timeUnit") ?: 300) as Int
        set(value) = Config.challengeDataConfig.setSetting(id, "timeUnit", value)
    private var time
        get() = (Config.challengeDataConfig.getSetting(id, "time") ?: 0) as Int
        set(value) = Config.challengeDataConfig.setSetting(id, "time", value)
    private var minTime: Int = 10

    private val materials = Material.values().filter { material -> material.isItem }

    override val id: String = "random-item"
    override val material: Material = Material.BEACON
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = translatable(nameKey).coloredString(locale)
        defaultPage = 0
        page(0) {
            placeholder(Slots.Border, placeHolderItemGray)
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            button(
                Slots.RowThreeSlotOne,
                getGoBackItem(locale)
            ) {
                it.player.openGUI(settingsGUI(locale), GUIPage.challengesPageNumber)
            }

            button(
                Slots.RowThreeSlotFour,
                distanceItem(locale)
            ) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    isDistance = !isDistance
                    it.bukkitEvent.currentItem = distanceItem(locale)
                } else if (it.bukkitEvent.isRightClick) {
                    it.guiInstance.gotoPage(1)
                }
            }

            button(
                Slots.RowThreeSlotSix,
                timeItem(locale)
            ) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    isTime = !isTime
                    it.bukkitEvent.currentItem = timeItem(locale)
                } else if (it.bukkitEvent.isRightClick) {
                    it.guiInstance.gotoPage(2)
                }
            }
        }

        // distance settings
        page(1) {
            placeholder(Slots.Border, placeHolderItemGray)
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            pageChanger(
                Slots.RowThreeSlotOne,
                getGoBackItem(locale),
                0,
                null,
                null
            )

            button(
                Slots.RowThreeSlotSix,
                plusItem("distance", "${distanceUnit}m", "10m", "100m", locale)
            ) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    distanceUnit += 10
                } else if (it.bukkitEvent.isRightClick) {
                    distanceUnit += 100
                }
                updateInventory(it.bukkitEvent.inventory, false, locale)
            }

            button(
                Slots.RowThreeSlotFive,
                resetItem("distance", "${distanceUnit}m", "500m", locale)
            ) {
                it.bukkitEvent.isCancelled = true
                distanceUnit = 500
                updateInventory(it.bukkitEvent.inventory, false, locale)
            }

            button(
                Slots.RowThreeSlotFour,
                minusItem("distance", "${distanceUnit}m", "10m", "100m", locale)
            ) {
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
                updateInventory(it.bukkitEvent.inventory, false, locale)
            }
        }

        // time settings
        page(2) {
            placeholder(Slots.Border, placeHolderItemGray)
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            pageChanger(
                Slots.RowThreeSlotOne,
                getGoBackItem(locale),
                0,
                null,
                null
            )

            button(
                Slots.RowThreeSlotSix,
                plusItem("time", Timer.formatTime(timeUnit.toLong(), locale).plainText(), "10s", "1m", locale)
            ) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    timeUnit += 10
                } else if (it.bukkitEvent.isRightClick) {
                    timeUnit += 60
                }
                updateInventory(it.bukkitEvent.inventory, true, locale)
            }

            button(
                Slots.RowThreeSlotFive,
                resetItem("time", Timer.formatTime(timeUnit.toLong(), locale).plainText(), "3m", locale)
            ) {
                it.bukkitEvent.isCancelled = true
                timeUnit = 300
                updateInventory(it.bukkitEvent.inventory, true, locale)
            }

            button(
                Slots.RowThreeSlotFour,
                minusItem("time", Timer.formatTime(timeUnit.toLong(), locale).plainText(), "10s", "1m", locale)
            ) {
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
                updateInventory(it.bukkitEvent.inventory, true, locale)
            }
        }
    }

    private fun updateInventory(inv: Inventory, isTime: Boolean, locale: Locale) {
        if (isTime) {
            inv.setItem(21, minusItem("time", Timer.formatTime(timeUnit.toLong(), locale).plainText(), "10s", "1m", locale))
            inv.setItem(22, resetItem("time", Timer.formatTime(timeUnit.toLong(), locale).plainText(), "3m", locale))
            inv.setItem(23, plusItem("time", Timer.formatTime(timeUnit.toLong(), locale).plainText(), "10s", "1m", locale))
        } else {
            inv.setItem(21, minusItem("distance", "${distanceUnit}m", "10m", "100m", locale))
            inv.setItem(22, resetItem("distance", "${distanceUnit}m", "500m", locale))
            inv.setItem(23, plusItem("distance", "${distanceUnit}m", "10m", "100m", locale))
        }
    }

    private fun resetItem(description: String, value: String, default: String, locale: Locale) =
        itemStack(Material.BARRIER) {
            meta {
                name = translatable("$id.reset_item.name")
                    .render(locale)

                addLore {
                    addComponent(
                        translatable("$id.reset_item.lore")
                            .args(
                                text(description),
                                text(value),
                                text(default)
                            )
                            .render(locale)
                    )
                }
            }
        }

    private fun plusItem(description: String, value: String, leftClick: String, rightClick: String, locale: Locale) =
        itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
            meta {
                name = translatable("$id.plus_item.name")
                    .render(locale)

                addLore {
                    addComponent(
                        translatable("$id.plus_item.lore")
                            .args(
                                text(description),
                                text(value),
                                text(leftClick),
                                text(rightClick)
                            )
                            .render(locale)
                    )
                }
            }
        }

    private fun minusItem(description: String, value: String, leftClick: String, rightClick: String, locale: Locale) =
        itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
            meta {
                name = translatable("$id.minus_item.name")
                    .render(locale)

                addLore {
                    addComponent(
                        translatable("$id.minus_item.lore")
                            .args(
                                text(description),
                                text(value),
                                text(leftClick),
                                text(rightClick)
                            )
                            .render(locale)
                    )
                }
            }
        }

    private fun distanceItem(locale: Locale) = itemStack(Material.GOLDEN_BOOTS) {
        meta {
            name = translatable("$id.distance_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.distance_item.lore")
                        .args(
                            if (isDistance) {
                                translatable("generic.enabled", TextColor.color(Color.GREEN.asRGB()))
                                    .append(text("($distanceUnit)m", TextColor.color(Color.GRAY.asRGB())))
                            } else {
                                translatable("generic.disabled", TextColor.color(Color.RED.asRGB()))
                            }
                        )
                        .render(locale)
                )
            }
        }
    }

    private fun timeItem(locale: Locale) = itemStack(Material.CLOCK) {
        meta {
            name = translatable("$id.time_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.distance_item.lore")
                        .args(
                            if (isDistance) {
                                translatable("generic.enabled", TextColor.color(Color.GREEN.asRGB()))
                                    .append(text("($timeUnit)s", TextColor.color(Color.GRAY.asRGB())))
                            } else {
                                translatable("generic.enabled", TextColor.color(Color.RED.asRGB()))
                            }
                        )
                        .render(locale)
                )
            }
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!isDistance ||
            !event.player.isPlaying() ||
            !event.hasChangedBlock()
        ) {
            return
        }

        distance++
        if (distance >= distanceUnit) {
            distance = 0
            onlinePlayers.filter { it.isPlaying() }.forEach { player ->
                player.give(ItemStack(materials.random()))
            }
        }
    }

    override fun update() {
        if (!isTime) return
        time++
        if (time >= timeUnit) {
            time = 0
            onlinePlayers.filter { it.isPlaying() }.forEach { player ->
                player.give(ItemStack(materials.random()))
            }
        }
    }
}
