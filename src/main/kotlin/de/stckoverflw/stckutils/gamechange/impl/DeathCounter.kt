package de.stckoverflw.stckutils.gamechange.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.gamechange.GameChange
import de.stckoverflw.stckutils.gamechange.active
import de.stckoverflw.stckutils.user.changesGUI
import de.stckoverflw.stckutils.user.goBackItem
import de.stckoverflw.stckutils.user.placeHolderItem
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent

object DeathCounter : GameChange() {
    override val id: String = "death-counter"
    override val name: String = "§9Death Counter"
    override val description: List<String> = listOf(
        " ",
        "§9Death Counter §7counts the Deaths of every",
        "§7player and displayes them in a Bossbar"
    )
    override val material: Material = Material.WITHER_SKELETON_SKULL
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryThreeByNine> = kSpigotGUI(GUIType.THREE_BY_NINE) {
        page(1) {
            placeholder(Slots.RowOneSlotOne rectTo Slots.RowThreeSlotNine, placeHolderItem)
            button(Slots.RowOneSlotOne, goBackItem) { it.player.openGUI(changesGUI()) }

            button(Slots.RowTwoSlotFive, deathItem()) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isLeftClick) {
                    deaths++
                } else if (it.bukkitEvent.isRightClick) {
                    if (deaths > 0) {
                        deaths--
                    } else {
                        it.player.sendMessage(StckUtilsPlugin.prefix + "§cYou can't have less then 0 deaths")
                    }
                }
                run()
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, deathItem())
            }
        }
    }

    override val defaultActivated: Boolean = false

    private val bossbar = Bukkit.createBossBar("§9Deaths: 0", BarColor.BLUE, BarStyle.SOLID)
    var deaths = 0

    override fun run() {
        if (active) {
            bossbar.isVisible = true
            Bukkit.getOnlinePlayers().forEach {
                bossbar.addPlayer(it)
                bossbar.progress = 1.0
            }
            bossbar.setTitle("§9Deaths: $deaths")
        } else {
            bossbar.isVisible = false
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        if (active) {
            deaths++
            run()
        }
    }

    private fun deathItem() = itemStack(Material.WITHER_SKELETON_SKULL) {
        meta {
            name = "§9Deaths"
            addLore {
                + " "
                + "§7Change the value of the Death Counter"
                + " "
                + "§7Left-click to higher the deaths"
                + "§7Right-click to lower the deaths"
            }
        }
    }
}