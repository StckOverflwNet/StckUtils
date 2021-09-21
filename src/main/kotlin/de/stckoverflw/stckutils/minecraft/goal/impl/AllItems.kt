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
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object AllItems : TeamGoal() {

    const val COMMAND_NAME = "allItems"
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

    private var filter: HashMap<UUID, Pair<Filter, Filter>> = HashMap()

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
            if (!isWon() && !Timer.additionalInfo.contains("collect ${formatMaterial(nextMaterial)}"))
                Timer.additionalInfo.add("collect ${formatMaterial(nextMaterial)}")
        }
    }

    fun resetFilter(player: Player) {
        filter[player.uniqueId] = Pair(Filter.ALL, Filter.ASCENDING)
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
                }, onClick = { clickEvent, _ ->
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
            button(Slots.RowFourSlotNine, filterItem(Pair(Filter.ALL, Filter.ASCENDING)), onClick = { clickEvent ->
                clickEvent.bukkitEvent.isCancelled = true
                val player = clickEvent.player
                if (filter[player.uniqueId] == null) resetFilter(player)
                if (clickEvent.bukkitEvent.isLeftClick) {
                    when (filter[player.uniqueId]!!.first) {
                        Filter.COLLECTED -> {
                            filter[player.uniqueId] = filter[player.uniqueId]!!.copy(
                                first = Filter.NOT_COLLECTED
                            )
                        }
                        Filter.NOT_COLLECTED -> {
                            filter[player.uniqueId] = filter[player.uniqueId]!!.copy(
                                first = Filter.ALL
                            )
                        }
                        else -> {
                            filter[player.uniqueId] = filter[player.uniqueId]!!.copy(
                                first = Filter.COLLECTED
                            )
                        }
                    }
                }
                if (clickEvent.bukkitEvent.isRightClick) {
                    filter[player.uniqueId] = when (filter[player.uniqueId]!!.second) {
                        Filter.ASCENDING -> {
                            filter[player.uniqueId]!!.copy(
                                second = Filter.DESCENDING
                            )
                        }
                        else -> {
                            filter[player.uniqueId]!!.copy(
                                second = Filter.ASCENDING
                            )
                        }
                    }
                }
                compound.sortContentBy(filter[player.uniqueId]!!.second == Filter.DESCENDING) { it.name }
                compound.setContent(getContent(filter[player.uniqueId]!!))
                clickEvent.guiInstance.reloadCurrentPage()

                clickEvent.guiInstance[Slots.RowFourSlotNine] = filterItem(filter[player.uniqueId]!!)
            })
            if (!isWon()) {
                button(Slots.RowThreeSlotOne, skipItem(), onClick = { clickEvent ->
                    if (clickEvent.bukkitEvent.isLeftClick) {
                        collected(StckUtilsPlugin.prefix + "§a${clickEvent.player.name} skipped ${formatMaterial(nextMaterial)}", false)
                    } else if (clickEvent.bukkitEvent.isRightClick) {
                        collected(StckUtilsPlugin.prefix + "§a${clickEvent.player.name} marked ${formatMaterial(nextMaterial)} as collected")
                    }
                    compound.sortContentBy(filter[clickEvent.player.uniqueId]!!.second == Filter.DESCENDING) { it.name }
                    compound.setContent(getContent(filter[clickEvent.player.uniqueId]!!))
                    clickEvent.guiInstance.reloadCurrentPage()
                    if (!isWon()) {
                        clickEvent.guiInstance[Slots.RowThreeSlotOne] = skipItem()
                    } else {
                        clickEvent.guiInstance[Slots.RowThreeSlotOne] = placeHolderItemGray
                    }
                })
            }
            compound.addContent(getContent(Pair(Filter.ALL, Filter.ASCENDING)))
            compound.sortContentBy(false) { it.name }

            compoundScroll(
                Slots.RowOneSlotNine,
                ItemStack(Material.PAPER), compound, scrollTimes = 1
            )
            compoundScroll(
                Slots.RowFiveSlotNine,
                ItemStack(Material.PAPER), compound, scrollTimes = 1, reverse = true
            )
        }
        onClose {
            resetFilter(it.player)
        }
    }

    private fun getContent(filter: Pair<Filter, Filter>): List<Material> {
        return if (filter.second == Filter.ASCENDING) {
            when (filter.first) {
                Filter.COLLECTED -> allItems.sortedBy { it.name }
                Filter.NOT_COLLECTED -> materials.minus(allItems).sortedBy { it.name }
                else -> materials.sortedBy { it.name }
            }
        } else {
            when (filter.first) {
                Filter.COLLECTED -> allItems.sortedBy { it.name }.asReversed()
                Filter.NOT_COLLECTED -> materials.minus(allItems).sortedBy { it.name }.asReversed()
                else -> materials.sortedBy { it.name }.asReversed()
            }
        }
    }

    private fun skipItem() = itemStack(Material.BEDROCK) {
        meta {
            name = "§eSkip current Item (${formatMaterial(nextMaterial)})"
            addLore {
                +" "
                +"§7LMB -> Skips §f${formatMaterial(nextMaterial)}"
                +"§7RMB -> Marks §f${formatMaterial(nextMaterial)}§7 as collected"
            }
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
            flag(ItemFlag.HIDE_ATTRIBUTES)
            flag(ItemFlag.HIDE_DESTROYS)
            flag(ItemFlag.HIDE_DYE)
            flag(ItemFlag.HIDE_PLACED_ON)
            flag(ItemFlag.HIDE_POTION_EFFECTS)
            flag(ItemFlag.HIDE_UNBREAKABLE)
        }
    }

    private fun filterItem(filter: Pair<Filter, Filter>) = itemStack(Material.HOPPER) {
        meta {
            name = "§8Filter"
            addLore {
                +" "
                +"§7LMB - ".plus(if (filter.first == Filter.ALL) "§d" else if (filter.first == Filter.COLLECTED) "§a" else "§c")
                    .plus(filter.first.name)
                +"§7RMB - ".plus(if (filter.second == Filter.ASCENDING) "§a" else "§c").plus(filter.second.name)
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

    private fun formatMaterial(material: Material): String {
        return material.name.lowercase().replace('_', ' ').split(' ').joinToString(" ", transform = { s ->
            s.replaceFirstChar { it.titlecase(Locale.getDefault()) }
        })
    }

    private fun randomMaterial(): Material {
        if (!isWon()) {
            return materials.filter { !isCollected(it) }.random()
        } else {
            error("Goal already finished")
        }
    }

    private fun isWon() = materials.none { !isCollected(it) }

    private fun collected(message: String, markCollected: Boolean = true) {
        broadcast(message)
        Timer.additionalInfo.remove("collect ${formatMaterial(nextMaterial)}")
        if (markCollected)
            allItems = ArrayList(allItems.plus(nextMaterial))
        if (isWon()) {
            Config.goalConfig.setSetting(id, "nextMaterial", null)
            win("You collected all Items!")
        } else {
            nextMaterial = randomMaterial()
            Timer.additionalInfo.add("collect ${formatMaterial(nextMaterial)}")
        }
    }

    private fun isCollected(material: Material) = allItems.contains(material)

    @EventHandler
    fun onCollectMove(event: InventoryClickEvent) {
        if (event.currentItem?.type == nextMaterial && (event.whoClicked as Player).isPlaying())
            collected(StckUtilsPlugin.prefix + "§a${event.whoClicked.name} collected ${formatMaterial(nextMaterial)}")
    }

    @EventHandler
    fun onCollectPickup(event: PlayerAttemptPickupItemEvent) {
        if (event.item.itemStack.type == nextMaterial && event.player.isPlaying())
            collected(StckUtilsPlugin.prefix + "§a${event.player.name} collected ${formatMaterial(nextMaterial)}")
    }
}
