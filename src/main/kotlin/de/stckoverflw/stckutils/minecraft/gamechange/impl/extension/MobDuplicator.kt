package de.stckoverflw.stckutils.minecraft.gamechange.impl.extension

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.minecraft.gamechange.GameExtension
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

object MobDuplicator : GameExtension() {
    override val id: String = "mob-duplicator"
    override val usesEvents: Boolean = true

    override fun item() = itemStack(Material.SUSPICIOUS_STEW) {
        meta {
            name = "§dMob Duplicator"
            addLore {
                +" "
                if (exponential) {
                    +"§7Every time you kill a mob it §cexponentially §7duplicates itself"
                    +"§7(max §c64 §7to prevent overloading the server)"
                } else {
                    +"§7Every time you kill a mob it duplicates itself "
                }
                +" "
                +"§7Exponential duplication currently ".plus(if (exponential) "§aactivated" else "§cdeactivated")
                +" "
                +"§7Currently ".plus(if (active) "§aactivated" else "§cdeactivated")
                /*
                * Shift click to reset exponential number
                 */
            }
        }
    }

    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        event.bukkitEvent.isCancelled = true
        if (event.bukkitEvent.isLeftClick) {
            active = !active
        } else if (event.bukkitEvent.isRightClick) {
            exponential = !exponential
        }

        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item())
    }

    private var active: Boolean
        get() = Config.gameChangeConfig.getSetting(id, "active") as Boolean? ?: true
        set(value) = Config.gameChangeConfig.setSetting(id, "active", value)
    private var amount: Int = 2
    private var exponential: Boolean = false

    override fun run() {
        // since this method is empty we don't ever call it
    }

    @EventHandler
    fun onDeath(event: EntityDamageByEntityEvent) {
        if (!active || event.entity !is Mob || event.damager !is Player || event.entity is EnderDragon || (event.entity as Mob).health - event.damage > 0) return
        for (i in 1..amount) {
            event.entity.world.spawnEntity(event.entity.location, event.entity.type)
        }
        if (exponential) if (amount * 2 > 64) amount = 64 else amount *= 2

    }
}
