package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.util.goBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.gui.*
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

object MobDuplicator : Challenge() {

    private var exponentialAmount: Int
        get() = Config.gameChangeConfig.getSetting(id, "exponentialAmount") as Int? ?: 2
        set(value) = Config.gameChangeConfig.setSetting(id, "exponentialAmount", value)
    private var isExponential: Boolean
        get() = Config.gameChangeConfig.getSetting(id, "isExponential") as Boolean? ?: false
        set(value) = Config.gameChangeConfig.setSetting(id, "isExponential", value)

    override val id: String = "mob-duplicator"
    override val name: String = "§dMob Duplicator"
    override val material: Material = Material.SUSPICIOUS_STEW
    override val description: List<String> = listOf(
        " ",
        "§7Every time you kill a mob it duplicates itself ",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = name
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, goBackItem) { it.player.openGUI(settingsGUI(), 1) }

            button(Slots.RowThreeSlotFive, exponentialItem()) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isShiftClick) {
                    exponentialAmount = 2
                } else {
                    isExponential = !isExponential
                }
                it.bukkitEvent.currentItem = exponentialItem()
            }
        }
    }

    private fun exponentialItem() = itemStack(Material.RABBIT_STEW) {
        meta {
            name = "§aExponential Duplication"
            addLore {
                +" "
                +"§7Toggle exponential duplication (capped at 64)"
                if (isExponential) {
                    +" "
                    +"§7 Shift Click to reset amount"
                }
                +" "
                +"§7Currently ".plus(if (isExponential) "§aenabled ($exponentialAmount)" else "§cdisabled")
            }
        }
    }

    @EventHandler
    fun onDeath(event: EntityDamageByEntityEvent) {
        if (event.entity !is Mob && event.damager !is Player) {
            return
        }
        val mob = event.entity as Mob
        val player = event.damager as Player

        // ignore players that are currently not playing
        if (!player.isPlaying()) {
            return
        }

        // ignore the EnderDragon and mobs that do not die after the damage is applied
        if (mob is EnderDragon || mob.health - event.damage > 0) {
            return
        }

        for (i in 1..exponentialAmount) {
            event.entity.world.spawnEntity(event.entity.location, event.entity.type)
        }
        if (isExponential && exponentialAmount * 2 <= 64) exponentialAmount *= 2
    }
}
