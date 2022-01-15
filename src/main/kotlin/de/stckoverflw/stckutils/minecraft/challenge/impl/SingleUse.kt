package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.util.Locale

object SingleUse : Challenge() {

    override val id: String = "single-use"
    override val material: Material = Material.WOODEN_PICKAXE
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return

        val player = event.whoClicked as Player
        if (!player.isPlaying()) return

        if (event.currentItem != null) {
            setOneDurability(event.currentItem!!)
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (event.player !is Player) return
        val player = event.player as Player
        player.inventory.contents?.forEach {
            if (it != null) {
                setOneDurability(it)
            }
        }
    }

    private fun setOneDurability(itemStack: ItemStack) {
        val itemMeta = itemStack.itemMeta ?: return
        if (itemMeta is Damageable) {
            itemMeta.damage = itemStack.type.maxDurability - 1
        }
        itemStack.itemMeta = itemMeta
    }
}
