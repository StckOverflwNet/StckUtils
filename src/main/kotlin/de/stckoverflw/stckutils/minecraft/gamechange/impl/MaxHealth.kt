package de.stckoverflw.stckutils.minecraft.gamechange.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.minecraft.gamechange.GameExtension
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.inventory.ItemStack

object MaxHealth : GameExtension() {
    override val id: String = "max-health"
    override val item: ItemStack = maxHealthItem()

    override val usesEvents: Boolean = false
    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        if (event.bukkitEvent.isLeftClick) {
            if (health >= 10) {
                if (health < 100) {
                    health += 10
                } else {
                    event.player.sendMessage(StckUtilsPlugin.prefix + "§cThe Maximal Health is reached")
                }
            } else {
                health += 1
            }
        } else if (event.bukkitEvent.isRightClick) {
            if (health > 10) {
                health -= 10
            } else {
                if (health > 1) {
                    health -= 1
                } else {
                    event.player.sendMessage(StckUtilsPlugin.prefix + "§cThe Minimal Health is reached")
                }
            }
        }
        run()
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, maxHealthItem())
    }

    private var health: Int = 20

    override fun run() {
        Bukkit.getOnlinePlayers().forEach {
            it.isHealthScaled = true
            it.healthScale = health.toDouble()
            it.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = health.toDouble()
            it.health = health.toDouble()
        }
    }

    private fun maxHealthItem() = itemStack(Material.REDSTONE) {
        meta {
            name = "§aMax Health"
            addLore {
                +" "
                +"§7Current value: §6$health"
                +"§7Default: §620"
                +" "
                +"§7Left-click to higher"
                +"§7Right-click to lower"
            }
        }
    }
}
