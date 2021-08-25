package de.stckoverflw.stckutils.minecraft.gamechange.impl

import de.stckoverflw.stckutils.minecraft.gamechange.GameExtension
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

object MobMagnet : GameExtension() {
    override val id: String = "mob-magnet"
    override val usesEvents: Boolean = true

    override val item: ItemStack = mobMagnetItem()

    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        event.bukkitEvent.isCancelled = true
        if (event.bukkitEvent.isLeftClick) {
            active = !active
        }

        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, mobMagnetItem())
    }

    private var active: Boolean = false

    override fun run() {
        // since this method is empty we don't ever call it
    }

    @EventHandler
    fun onDeath(event: EntityDamageByEntityEvent) {
        if (!active || event.entity !is Mob || event.damager !is Player || (event.entity as Mob).health - event.damage > 0) return

        event.entity.getNearbyEntities(48.0, 48.0, 48.0).forEach { entity ->
            if (entity.type == event.entity.type) {
                entity.teleport(event.entity.location)
            }
        }
    }

    private fun mobMagnetItem() = itemStack(Material.RAW_IRON_BLOCK) {
        meta {
            name = "§dMob Magnet"
            addLore {
                +" "
                +"§7Every time you kill a mob all nearby mobs"
                +"§7of that type will be teleported to the killed mob"
                +" "
                +"§7Currently ".plus(if (active) "§aactivated" else "§cdeactivated")
            }
        }
    }
}
