package de.stckoverflw.stckutils.gamechange.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.gamechange.GameChange
import de.stckoverflw.stckutils.gamechange.active
import de.stckoverflw.stckutils.user.changesGUI
import de.stckoverflw.stckutils.user.goBackItem
import de.stckoverflw.stckutils.user.placeHolderItem
import de.stckoverflw.stckutils.user.settingsGUI
import net.axay.kspigot.extensions.bukkit.heal
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute

object MaxHealth : GameChange() {
    override val id: String = "max-health"
    override val name: String = "§cMax Health"
    override val material: Material = Material.REDSTONE
    override val description: List<String> = listOf(
        " ",
        "§7Changes the maximal Health a Player can have"
    )

    override val usesEvents: Boolean = false
    override val defaultActivated: Boolean = false

    private var health: Double = 20.0

    override fun configurationGUI(): GUI<ForInventoryThreeByNine> = kSpigotGUI(GUIType.THREE_BY_NINE) {
        title = name
        page(1) {
            placeholder(Slots.RowOneSlotOne rectTo Slots.RowThreeSlotNine, placeHolderItem)
            button(Slots.RowOneSlotOne, goBackItem) { it.player.openGUI(changesGUI()) }
            button(Slots.RowTwoSlotFive, healthItem()) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    if (health >= 20) {
                        if (health < 100) {
                            health += 10
                        } else {
                            it.player.sendMessage(StckUtilsPlugin.prefix + "§cThe Maximal Health is reached")
                        }
                    } else {
                        health += 1
                    }
                } else if (it.bukkitEvent.isRightClick) {
                    if (health > 10) {
                        health -= 10
                    } else {
                        if (health > 1) {
                            health -= 1
                        } else {
                            it.player.sendMessage(StckUtilsPlugin.prefix + "§cThe Minimal Health is reached")
                        }
                    }
                }
                run()
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, healthItem())
            }
        }
    }

    override fun run() {
        Bukkit.getOnlinePlayers().forEach {
            if (active) {
                it.isHealthScaled = true
                it.healthScale = health
                it.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = health
                it.health = health
            } else {
                it.isHealthScaled = true
                it.healthScale = 20.0
                it.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = 20.0
                it.health = health
            }
        }
    }


    private fun healthItem() = itemStack(Material.REDSTONE) {
        meta {
            name = "§cMax Health"
            addLore {
                + " "
                + "§7Current value: §6$health"
                + "§7Default: §620"
                + " "
                + "§7Left-click to higher"
                + "§7Right-click to lower"
            }
        }
    }
}