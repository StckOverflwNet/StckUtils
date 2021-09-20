package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isObtainableInSurvival
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.goal.TeamGoal
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.placeHolderItemGray
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.*
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import kotlin.collections.ArrayList

object AllItems : TeamGoal() {

    const val COMMAND_NAME = "allitems"
    private var allItems: ArrayList<Material>
        get() {
            val list: MutableList<*>? = Config.goalConfig.getSettingList(id, "allItems")
            return if (list == null || list.isEmpty()) ArrayList()
            else ArrayList(list.filterNotNull().map { Material.valueOf(it as String) })
        }
        set(value) = Config.goalConfig.setSetting(id, "allItems", value.map { it.name })
    private val materials: List<Material> = Material.values().filter { it.isObtainableInSurvival() }
    private var nextMaterial: Material
        get() {
            val mat = randomMaterial().toString()
            var material = Config.goalConfig.getSetting(id, "nextMaterial")
            if (material == null) {
                Config.goalConfig.setSetting(id, "nextMaterial", mat)
                material = mat
            }
            return Material.valueOf(material as String)
        }
        set(value) = Config.goalConfig.setSetting(id, "nextMaterial", value.name)

    private var filter: Pair<Filter, Filter> = Pair(Filter.ALL, Filter.ASCENDING)

    private enum class Filter {
        COLLECTED,
        NOT_COLLECTED,
        ALL,
        ASCENDING,
        DESCENDING;
    }

    override val id: String = "all-items"
    override val name: String = "§aAll Items"
    override val description: List<String> = listOf(
        " ",
        "§7Collect all Items",
    )
    override val material: Material = Material.ENDER_CHEST
    override fun onTimerToggle() {
        if (Timer.running) {
            if (!Timer.additionalInfo.contains("collect ${formatMaterial(nextMaterial)}"))
                Timer.additionalInfo.add("collect ${formatMaterial(nextMaterial)}")
        }
    }

    fun gui() = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = name
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)

            val compound = createRectCompound<Material>(
                Slots.RowTwoSlotTwo, Slots.RowFourSlotEight,
                iconGenerator = {
                    generateAllItemsItem(it)
                }, onClick = { clickEvent, material ->
                clickEvent.bukkitEvent.isCancelled = true
            }
            )

            // Reset Item
            button(Slots.RowTwoSlotNine, resetItem()) {
                allItems = ArrayList()
                nextMaterial = randomMaterial()
                it.guiInstance.reloadCurrentPage()
                broadcast(StckUtilsPlugin.prefix + "§4${it.bukkitEvent.whoClicked.name} reset the progress of All Items")
            }

            // Filter Button (Filter only collected/only not collected/all)
            button(Slots.RowFourSlotNine, filterItem(filter)) {
                if (it.bukkitEvent.isLeftClick) {
                    when (filter.first) {
                        Filter.COLLECTED -> {
                            compound.setContent(materials.minus(allItems))
                            filter = filter.copy(
                                first = Filter.NOT_COLLECTED
                            )
                        }
                        Filter.NOT_COLLECTED -> {
                            compound.setContent(allItems)
                            filter = filter.copy(
                                first = Filter.ALL
                            )
                        }
                        else -> {
                            compound.setContent(materials)
                            filter = filter.copy(
                                first = Filter.COLLECTED
                            )
                        }
                    }
                } else {
                    filter = when (filter.second) {
                        Filter.ASCENDING -> {
                            compound.sortContentBy(false) { mat -> mat.name }
                            filter.copy(
                                second = Filter.DESCENDING
                            )
                        }
                        else -> {
                            compound.sortContentBy(true) { mat -> mat.name }
                            filter.copy(
                                second = Filter.ASCENDING
                            )
                        }
                    }
                }
                it.bukkitEvent.currentItem = filterItem(filter)
                println("${filter.first} - ${filter.second}")

                it.guiInstance.reloadCurrentPage()
            }

            compound.addContent(materials.sortedBy { it.name })

            compoundScroll(
                Slots.RowOneSlotNine,
                ItemStack(Material.PAPER), compound, scrollTimes = 1
            )
            compoundScroll(
                Slots.RowFiveSlotNine,
                ItemStack(Material.PAPER), compound, scrollTimes = 1, reverse = true
            )
        }
    }

    private fun generateAllItemsItem(material: Material) = itemStack(material) {
        meta {
            name = "§7${formatMaterial(material)}"
            addLore {
                +" "
                +if (isCollected(material)) "§7[§a+§7] §aCollected" else "§7[§c~§7] §cNot Collected"
            }
            if (isCollected(material)) {
                addEnchant(Enchantment.ARROW_INFINITE, 1, true)
                flag(ItemFlag.HIDE_ENCHANTS)
            }
        }
    }

    private fun filterItem(setting: Pair<Filter, Filter>) = itemStack(Material.HOPPER) {
        meta {
            name = "§8Filter"
            addLore {
                +" "
                +"§7LMB - ${setting.first}"
                +"§7RMB - ${setting.second}"
            }
        }
    }

    private fun resetItem() = itemStack(Material.BARRIER) {
        meta {
            name = "§4Reset"
            addLore {
                +" "
                +"§7Reset the progress of All Items"
            }
        }
    }

    private fun formatMaterial(material: Material) = material.name.lowercase()

    private fun randomMaterial() = materials.filter { !allItems.contains(it) }.random()

    private fun collected(player: Player) {
        broadcast(StckUtilsPlugin.prefix + "§a${player.name} collected ${formatMaterial(nextMaterial)}")
        allItems = ArrayList(allItems.plus(nextMaterial))
        Timer.additionalInfo.remove("collect ${formatMaterial(nextMaterial)}")
        nextMaterial = randomMaterial()
        Timer.additionalInfo.add("collect ${formatMaterial(nextMaterial)}")
    }

    private fun isCollected(material: Material) = allItems.contains(material)

    @EventHandler
    fun onCollectMove(event: InventoryMoveItemEvent) {
        if (event.item.type == nextMaterial && event.initiator.viewers.isNotEmpty() && event.initiator.viewers[0] is Player && (event.initiator.viewers[0] as Player).isPlaying())
            collected(event.initiator.viewers[0] as Player)
    }

    @EventHandler
    fun onCollectPickup(event: PlayerAttemptPickupItemEvent) {
        if (event.item.itemStack.type == nextMaterial && event.player.isPlaying())
            collected(event.player)
    }
}
