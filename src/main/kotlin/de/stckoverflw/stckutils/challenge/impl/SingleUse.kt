package de.stckoverflw.stckutils.challenge.impl

import de.stckoverflw.stckutils.challenge.Challenge
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.gui.ForInventoryThreeByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

object SingleUse : Challenge() {
    override val id: String = "single-use"
    override val name: String = "${KColors.INDIANRED}Single use"
    override val material: Material = Material.WOODEN_PICKAXE
    override val description: List<String> = listOf(
        " ",
        "§7§lEvery §7Item has §c1 durability §7and",
        "§7can just be used §conce"
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryThreeByNine>? = null

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.currentItem != null) {
            try {
                val itemStack = event.currentItem!!
                val itemMeta = itemStack.itemMeta ?: return
                if (itemMeta is Damageable) {
                    val damage = (itemMeta as Damageable).damage
                    (itemMeta as Damageable).damage = itemStack.type.maxDurability - 1
                }
                itemStack.itemMeta = itemMeta
                event.currentItem = itemStack
            } catch (ignored: ClassCastException) {
                ignored.printStackTrace()
            }
        }
    }
}