package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.coloredString
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.util.GUIPage
import de.stckoverflw.stckutils.util.getGoBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.extensions.geometry.plus
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.gui.rectTo
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.Locale

object Randomizer : Challenge() {

    private var randomizeEverything: Boolean
        get() = (Config.challengeConfig.getSetting(id, "randomizeEverything") ?: true) as Boolean
        set(value) = Config.challengeConfig.setSetting(id, "randomizeEverything", value)
    private val drops = HashMap<Material, Material>()

    override val id: String = "randomizer"
    override val material: Material = Material.COMMAND_BLOCK
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = translatable(nameKey).coloredString(locale)
        defaultPage = 1
        page(1) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, getGoBackItem(locale)) { it.player.openGUI(settingsGUI(locale), GUIPage.challengesPageNumber) }

            button(Slots.RowThreeSlotFive, randomizerSettingsItem()) {
                randomizeEverything = !randomizeEverything
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, randomizerSettingsItem())
            }
        }
    }

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

    private fun randomizerSettingsItem() =
        if (randomizeEverything) itemStack(Material.SUNFLOWER) {
            meta {
                name = translatable("$id.randomizer_item.name")
                addLore {
                    addComponent(translatable("$id.randomizer_item.lore.random"))
                }
            }
        } else itemStack(Material.MINECART) {
            meta {
                name = translatable("$id.randomizer_item.name")
                addLore {
                    addComponent(translatable("$id.randomizer_item.lore.all_random"))
                }
            }
        }
}
