package de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.minecraft.gamechange.GameRule
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object KeepInventory : GameRule() {
    override val id: String = "keep-inventory"

    override fun item(): ItemStack = itemStack(Material.COMMAND_BLOCK) {
        meta {
            name = "§6Keep Inventory"
            addLore {
                + " "
                if (keepInventory) {
                    + "§7You currently §akeep your Inventory§7,"
                } else {
                    + "§7You currently §ckeep your inventory §c§lnot§7,"
                }
                + "§7when you die"
            }
        }
    }

    override val usesEvents: Boolean = false

    private var keepInventory: Boolean
        get() = Config.gameChangeConfig.getSetting(id, "keep") as Boolean? ?: false
        set(value) = Config.gameChangeConfig.setSetting(id, "keep", value)

    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        keepInventory = !keepInventory
        run()
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item())
    }

    override fun run() {
        Bukkit.getWorlds().forEach {
            it.setGameRule(org.bukkit.GameRule.KEEP_INVENTORY, keepInventory)
        }
    }


}