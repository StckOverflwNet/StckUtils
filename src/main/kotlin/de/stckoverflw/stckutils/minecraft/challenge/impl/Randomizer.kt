package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.util.getGoBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.extensions.geometry.plus
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

object Randomizer : Challenge() {

    override val id: String = "randomizer"
    override val name: String = "§eRandomizer"

    override val material: Material = Material.COMMAND_BLOCK

    override val description: List<String> = listOf(
        " ",
        "§7Randomizes Block drops are randomized"
    )
    override val usesEvents: Boolean = true

    private var randomizeEverything: Boolean
        get() = (Config.challengeConfig.getSetting(id, "randomizeEverything") ?: true) as Boolean
        set(value) = Config.challengeConfig.setSetting(id, "randomizeEverything", value)

    private val drops = HashMap<Material, Material>()

    @EventHandler
    fun onBlockBreak(event: BlockDropItemEvent) {
        if (drops.isEmpty()) {
            val newValues = Material.values().filter { it.isItem || it.isSolid }.shuffled()
            Material.values().filter { it.isItem || it.isSolid }.zip(newValues).forEach {
                drops[it.first] = it.second
            }
        }
        if (!randomizeEverything) {
            event.items.removeAll { true }
            event.player.world.dropItem(event.block.location, ItemStack(drops[event.blockState.type]!!))
        } else {
            event.items.removeAll { true }
            event.player.world.dropItem(
                event.block.location.clone().plus(Vector(0.0, 0.5, 0.0)),
                ItemStack(Material.values().filter { it.isItem }.random())
            )
        }
    }

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = name
        defaultPage = 1
        page(1) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, getGoBackItem(locale)) { it.player.openGUI(settingsGUI(locale), 1) }

            button(Slots.RowThreeSlotFive, randomizerSettingsItem()) {
                randomizeEverything = !randomizeEverything
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, randomizerSettingsItem())
            }
        }
    }

    private fun randomizerSettingsItem() = if (randomizeEverything) itemStack(Material.SUNFLOWER) {

        meta {
            name = "§eRandom Item"
            addLore {
                + " "
                + "§7Every Block drops a random Item"
                + " "
                + "§7Click to change to Randomized Drops"
            }
        }
    } else itemStack(Material.MINECART) {
        meta {
            name = "§eRandom Item"
            addLore {
                + " "
                + "§7All Block-Drops are randomized"
                + " "
                + "§7Click to change to Random Item"
            }
        }
    }
}
