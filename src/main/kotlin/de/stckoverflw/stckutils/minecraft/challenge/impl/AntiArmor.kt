package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

// https://canary.discord.com/channels/484676017513037844/534731376352559124/923661675784183908
object AntiArmor : Challenge() {

    override val id: String = "anti-armor"
    override val name: String = "§3AntiArmor"
    override val material: Material = Material.IRON_HELMET
    override val description: List<String> = listOf(
        " ",
        "§7You loose health if you wear armor",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    /*
     * PlayerArmorChangeEvent is currently not supported
     */
    override fun update() {
        onlinePlayers.filter { it.isPlaying() }.forEach { player ->
            handlePlayerArmor(player.inventory, player)
        }
    }

    private fun handlePlayerArmor(inventory: PlayerInventory, player: Player) {
        var toRemovingHealth = 0
        toRemovingHealth += when (inventory.helmet?.type) {
            Material.LEATHER_HELMET -> 1
            Material.GOLDEN_HELMET, Material.CHAINMAIL_HELMET -> 2
            Material.IRON_HELMET -> 3
            Material.DIAMOND_HELMET -> 4
            else -> 0
        }
        toRemovingHealth += when(inventory.chestplate?.type) {
            Material.LEATHER_CHESTPLATE -> 3
            Material.GOLDEN_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE -> 4
            Material.IRON_CHESTPLATE -> 5
            Material.DIAMOND_CHESTPLATE -> 6
            else -> 0
        }
        toRemovingHealth += when(inventory.leggings?.type) {
            Material.LEATHER_LEGGINGS -> 2
            Material.GOLDEN_LEGGINGS, Material.CHAINMAIL_LEGGINGS -> 3
            Material.IRON_LEGGINGS -> 4
            Material.DIAMOND_LEGGINGS -> 5
            else -> 0
        }
        toRemovingHealth += when(inventory.boots?.type) {
            Material.LEATHER_BOOTS -> 2
            Material.GOLDEN_BOOTS, Material.CHAINMAIL_BOOTS -> 2
            Material.IRON_BOOTS -> 3
            Material.DIAMOND_BOOTS -> 4
            else -> 0
        }
        player.healthScale = 20.toDouble() - toRemovingHealth
    }

}