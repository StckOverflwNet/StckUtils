package de.stckoverflw.stckutils.minecraft.gamechange.impl.extension

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.minecraft.gamechange.GameExtension
import de.stckoverflw.stckutils.minecraft.gamechange.active
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
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

object DeathCounter : GameExtension() {
    override val id: String = "death-counter"
    override val usesEvents: Boolean = true

    override fun item() = itemStack(Material.WITHER_SKELETON_SKULL) {
        meta {
            name = "§9Death Counter"
            addLore {
                + " "
                + "§9Death Counter §7counts the Deaths of every"
                + "§7Player and displays them in a Bossbar"
                + " "
                + "§7Currently ".plus(if (active) "§aactivated" else "§cdeactivated")
                /*
                + "§7Shift Left-click to higher the deaths"
                + "§7Shift Right-click to lower the deaths"
                 */
            }
        }
    }

    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        event.bukkitEvent.isCancelled = true
        if (event.bukkitEvent.isShiftClick) {
            if (event.bukkitEvent.isLeftClick) {
                deaths++
            } else if (event.bukkitEvent.isRightClick) {
                if (deaths > 0) {
                    deaths--
                } else {
                    event.player.sendMessage(StckUtilsPlugin.prefix + "§cYou can't have less then 0 deaths")
                }
            }
        } else {
            active = !active
        }
        run()
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item())
    }

    private val bossbar = Bukkit.createBossBar("§9Deaths: 0", BarColor.BLUE, BarStyle.SOLID)
    private var deaths = 0

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
        deaths++
        run()
    }
}
