package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.coloredString
import de.stckoverflw.stckutils.extension.isObtainableInSurvival
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.minecraft.goal.TeamGoal
import de.stckoverflw.stckutils.minecraft.goal.nameKey
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.placeHolderItemGray
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.flag
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.space
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.Locale
import java.util.UUID

object AllItems : TeamGoal() {

    private var allItems: List<Material>
        get() {
            val list: MutableList<*>? = Config.allItemsDataConfig.getSettingList("allItems")
            return if (list == null || list.isEmpty()) listOf()
            else list.filterNotNull().map { Material.valueOf(it as String) }
        }
        set(value) = Config.allItemsDataConfig.setSetting("allItems", value.map { it.name })
    private val materials: List<Material> = Material.values().filter { it.isObtainableInSurvival() }
    private var nextMaterial: Material
        get() {
            var material = Config.allItemsDataConfig.getSetting("nextMaterial")
            if (material == null) {
                val mat = randomMaterial().toString()
                Config.allItemsDataConfig.setSetting("nextMaterial", mat)
                material = mat
            }
            return Material.valueOf(material as String)
        }
        set(value) = Config.allItemsDataConfig.setSetting("nextMaterial", value.name)

    private var filter: HashMap<UUID, Pair<Filter, Filter>> = HashMap()

    private enum class Filter {
        COLLECTED,
        NOT_COLLECTED,
        ALL,
        ASCENDING,
        DESCENDING;
    }

    override val id: String = "all-items"
    override val material: Material = Material.ENDER_CHEST

    override fun onTimerToggle() {
        if (Timer.running) {
            if (!isWon() && !Timer.additionalInfo.contains("collect ${formatMaterial(nextMaterial)}")) {
                Timer.additionalInfo.clear()
                Timer.additionalInfo.add("collect ${formatMaterial(nextMaterial)}")
            }
        }
    }

    fun resetFilter(player: Player) {
        filter[player.uniqueId] = Pair(Filter.ALL, Filter.ASCENDING)
    }

    fun gui(locale: Locale) = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = translatable(nameKey).coloredString(locale)
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)

            val compound = createRectCompound<Material>(
                Slots.RowTwoSlotTwo, Slots.RowFourSlotEight,
                iconGenerator = {
                    generateAllItemsItem(it, locale)
                }, onClick = { clickEvent, _ ->
                clickEvent.bukkitEvent.isCancelled = true
            }
            )

            // Reset Item
            button(Slots.RowTwoSlotNine, resetItem(locale)) {
                allItems = listOf()
                nextMaterial = randomMaterial()
                it.guiInstance.reloadCurrentPage()
                onlinePlayers.forEach { player ->
                    player.sendMessage(
                        translatable("$id.message.reset_progress", listOf(it.bukkitEvent.whoClicked.name()))
                    )
                }
            }

            // Filter Button (Filter only collected/only not collected/all)
            button(Slots.RowFourSlotNine, filterItem(Pair(Filter.ALL, Filter.ASCENDING), locale), onClick = { clickEvent ->
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

                clickEvent.guiInstance[Slots.RowFourSlotNine] = filterItem(filter[player.uniqueId]!!, locale)
            })
            if (!isWon()) {
                button(Slots.RowThreeSlotOne, skipItem(locale), onClick = { clickEvent ->
                    if (clickEvent.bukkitEvent.isLeftClick) {
                        collected(
                            "$id.skipped",
                            listOf(clickEvent.player.name(), text(formatMaterial(nextMaterial))),
                            false
                        )
                    } else if (clickEvent.bukkitEvent.isRightClick) {
                        collected(
                            "$id.marked",
                            listOf(clickEvent.player.name(), text(formatMaterial(nextMaterial)))
                        )
                    }
                    compound.sortContentBy(filter[clickEvent.player.uniqueId]!!.second == Filter.DESCENDING) { it.name }
                    compound.setContent(getContent(filter[clickEvent.player.uniqueId]!!))
                    clickEvent.guiInstance.reloadCurrentPage()
                    if (!isWon()) {
                        clickEvent.guiInstance[Slots.RowThreeSlotOne] = skipItem(locale)
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
                Filter.NOT_COLLECTED -> materials.minus(allItems.toSet()).sortedBy { it.name }
                else -> materials.sortedBy { it.name }
            }
        } else {
            when (filter.first) {
                Filter.COLLECTED -> allItems.sortedBy { it.name }.asReversed()
                Filter.NOT_COLLECTED -> materials.minus(allItems.toSet()).sortedBy { it.name }.asReversed()
                else -> materials.sortedBy { it.name }.asReversed()
            }
        }
    }

    private fun skipItem(locale: Locale) = itemStack(Material.BEDROCK) {
        meta {
            name = translatable("$id.skip_item.name", listOf(text(formatMaterial(nextMaterial))))
                .render(locale)
            addLore {
                addComponent(
                    translatable("$id.skip_item.lore", listOf(text(formatMaterial(nextMaterial))))
                        .render(locale)
                )
            }
        }
    }

    private fun generateAllItemsItem(material: Material, locale: Locale) = itemStack(material) {
        meta {
            name = text(formatMaterial(material))
            addLore {
                +space()
                addComponent(
                    if (isCollected(material)) {
                        translatable("$id.collected")
                            .render(locale)
                    } else {
                        translatable("$id.not_collected")
                            .render(locale)
                    }
                )
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

    private fun filterItem(filter: Pair<Filter, Filter>, locale: Locale) = itemStack(Material.HOPPER) {
        meta {
            name = translatable("$id.filter_item.name")
                .render(locale)
            addLore {
                addComponent(
                    translatable(
                        "$id.filter_item.lore",
                        listOf(
                            text(
                                filter.first.name,
                                TextColor.color(
                                    (if (filter.first == Filter.ALL) KColors.PURPLE else if (filter.first == Filter.COLLECTED) KColors.GREEN else KColors.RED).color.rgb
                                )
                            ),
                            text(
                                filter.second.name,
                                TextColor.color(
                                    (if (filter.second == Filter.ASCENDING) KColors.GREEN else KColors.RED).color.rgb
                                )
                            )
                        )
                    )
                        .render(locale)
                )
            }
        }
    }

    private fun resetItem(locale: Locale) = itemStack(Material.BARRIER) {
        meta {
            name = translatable("$id.reset_item.name")
                .render(locale)
            addLore {
                addComponent(
                    translatable("$id.reset_item.lore")
                        .render(locale)
                )
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

    private fun collected(key: String, replacements: List<Component> = listOf(), markCollected: Boolean = true) {
        onlinePlayers.forEach {
            it.sendMessage(
                translatable(key, replacements)
            )
        }
        Timer.additionalInfo.clear()
        if (markCollected)
            allItems = ArrayList(allItems.plus(nextMaterial))
        if (isWon()) {
            Config.allItemsDataConfig.setSetting("nextMaterial", null)
            win()
        } else {
            nextMaterial = randomMaterial()
            Timer.additionalInfo.add("collect ${formatMaterial(nextMaterial)}")
        }
    }

    private fun isCollected(material: Material) = allItems.contains(material)

    @EventHandler
    fun onCollectMove(event: InventoryClickEvent) {
        if (event.currentItem?.type == nextMaterial && (event.whoClicked as Player).isPlaying())
            collected("$id.collected_collected", listOf(event.whoClicked.name(), text(formatMaterial(nextMaterial))))
    }

    @EventHandler
    fun onCollectPickup(event: PlayerAttemptPickupItemEvent) {
        if (event.item.itemStack.type == nextMaterial && event.player.isPlaying())
            collected("$id.collected_collected", listOf(event.player.name(), text(formatMaterial(nextMaterial))))
    }
}
