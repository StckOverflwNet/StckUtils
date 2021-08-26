package de.stckoverflw.stckutils.minecraft.gamechange.impl

import de.stckoverflw.stckutils.minecraft.gamechange.GameExtension
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

object JackHammer : GameExtension() {
    override val id: String = "jackhammer"
    override val usesEvents: Boolean = true

    override val item: ItemStack = jackHammerItem()

    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        event.bukkitEvent.isCancelled = true
        if (event.bukkitEvent.isLeftClick) {
            active = !active
        }

        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, jackHammerItem())
    }

    private var active: Boolean = false

    override fun run() {
        // since this method is empty we don't ever call it
    }

    @EventHandler
    fun onDeath(event: BlockBreakEvent) {
        for (i in event.block.y downTo 0) {
            val block = event.block.world.getBlockAt(event.block.x, i, event.block.z)
            if (block.type.blastResistance > 1200.0F) return
            block.type = Material.AIR
        }
    }

    private fun jackHammerItem() = itemStack(Material.IRON_PICKAXE) {
        meta {
            name = "§eJackHammer"
            addLore {
                +" "
                +"§7When you break a block every block below it will break too"
                +"§7(Except Bedrock)"
                +" "
                +"§7Currently ".plus(if (active) "§aactivated" else "§cdeactivated")
            }
        }
    }
}
