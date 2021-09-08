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

object AllowPvP : GameRule() {
    override val id: String = "allow-pvp"
    override fun item() = itemStack(Material.DIAMOND_SWORD) {
        meta {
            name = "§aPvP"
            addLore {
                + " "
                + "§7Sets if Players can damage"
                + "§7each other"
                + " "
                if (pvp) {
                    + "§7Players §acan attack each other"
                } else {
                    + "§7Players §ccannot attack each other"
                }
                + "§7at the moment"
            }
        }
    }
    override val usesEvents: Boolean = false

    private var pvp: Boolean
        get() = Config.gameChangeConfig.getSetting(id, "pvp") as Boolean? ?: true
        set(value) = Config.gameChangeConfig.setSetting(id, "pvp", value)

    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        pvp = !pvp
        run()
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item())
    }

    override fun run() {
        Bukkit.getWorlds().forEach {
            it.pvp = pvp
        }
    }
}
