package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.coloredString
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.sendPrefixMessage
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
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.Locale
import java.util.UUID

object AllMobs : TeamGoal() {

    private var allMobs: List<EntityType>
        get() {
            val list: MutableList<*>? = Config.allMobsDataConfig.getSettingList("allMobs")
            return if (list == null || list.isEmpty()) listOf()
            else list.filterNotNull().map { EntityType.valueOf(it as String) }
        }
        set(value) = Config.allMobsDataConfig.setSetting("allMobs", value.map { it.name })
    private val mobs: List<EntityType> = EntityType.values().filter {
        it.isAlive && when (it) {
            EntityType.ILLUSIONER,
            EntityType.GIANT,
            EntityType.PLAYER,
            EntityType.ARMOR_STAND,
            -> false
            else -> true
        }
    }
    private var nextMob: EntityType
        get() {
            var entity = Config.allMobsDataConfig.getSetting("nextMob")
            if (entity == null) {
                val e = randomMob().toString()
                Config.allMobsDataConfig.setSetting("nextMob", e)
                entity = e
            }
            return EntityType.valueOf(entity as String)
        }
        set(value) = Config.allMobsDataConfig.setSetting("nextMob", value.name)

    private var filter: HashMap<UUID, Pair<Filter, Filter>> = HashMap()

    private enum class Filter {
        KILLED,
        NOT_KILLED,
        ALL,
        ASCENDING,
        DESCENDING;
    }

    override val id: String = "all-mobs"
    override val material = Material.SPAWNER

    override fun onTimerToggle() {
        if (Timer.running) {
            if (!isWon() && !Timer.additionalInfo.contains("kill ${formatMob(nextMob)}")) {
                Timer.additionalInfo.clear()
                Timer.additionalInfo.add("kill ${formatMob(nextMob)}")
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

            val compound = createRectCompound<EntityType>(
                Slots.RowTwoSlotTwo, Slots.RowFourSlotEight,
                iconGenerator = {
                    generateAllMobsItem(it)
                }, onClick = { clickEvent, _ ->
                clickEvent.bukkitEvent.isCancelled = true
            }
            )

            // Reset Item
            button(Slots.RowTwoSlotNine, resetItem()) {
                allMobs = listOf()
                nextMob = randomMob()
                it.guiInstance.reloadCurrentPage()
                onlinePlayers.forEach { player ->
                    player.sendPrefixMessage(
                        translatable("$id.reset_progress", listOf(it.bukkitEvent.whoClicked.name()))
                    )
                }
            }

            // Filter Button (Filter only collected/only not collected/all)
            button(Slots.RowFourSlotNine, filterItem(Pair(Filter.ALL, Filter.ASCENDING)), onClick = { clickEvent ->
                clickEvent.bukkitEvent.isCancelled = true
                val player = clickEvent.player
                if (filter[player.uniqueId] == null) resetFilter(player)
                if (clickEvent.bukkitEvent.isLeftClick) {
                    when (filter[player.uniqueId]!!.first) {
                        Filter.KILLED -> {
                            filter[player.uniqueId] = filter[player.uniqueId]!!.copy(
                                first = Filter.NOT_KILLED
                            )
                        }
                        Filter.NOT_KILLED -> {
                            filter[player.uniqueId] = filter[player.uniqueId]!!.copy(
                                first = Filter.ALL
                            )
                        }
                        else -> {
                            filter[player.uniqueId] = filter[player.uniqueId]!!.copy(
                                first = Filter.KILLED
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
                        collected(
                            "$id.skipped",
                            listOf(clickEvent.player.name(), text(formatMob(nextMob))),
                            false
                        )
                    } else if (clickEvent.bukkitEvent.isRightClick) {
                        collected(
                            "$id.marked",
                            listOf(clickEvent.player.name(), text(formatMob(nextMob))),
                            false
                        )
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

    private fun getContent(filter: Pair<Filter, Filter>): List<EntityType> {
        return if (filter.second == Filter.ASCENDING) {
            when (filter.first) {
                Filter.KILLED -> allMobs.sortedBy { it.name }
                Filter.NOT_KILLED -> mobs.minus(allMobs.toSet()).sortedBy { it.name }
                else -> mobs.sortedBy { it.name }
            }
        } else {
            when (filter.first) {
                Filter.KILLED -> allMobs.sortedBy { it.name }.asReversed()
                Filter.NOT_KILLED -> mobs.minus(allMobs.toSet()).sortedBy { it.name }.asReversed()
                else -> mobs.sortedBy { it.name }.asReversed()
            }
        }
    }

    private fun skipItem() = itemStack(Material.BEDROCK) {
        meta {
            name = translatable("$id.skip_item.name", listOf(text(formatMob(nextMob))))
            addLore {
                addComponent(translatable("$id.skip_item.lore", listOf(text(formatMob(nextMob)))))
            }
        }
    }

    private fun generateAllMobsItem(entity: EntityType) = itemStack(
        when (entity) {
            EntityType.SNOWMAN -> Material.SNOW_BLOCK
            EntityType.IRON_GOLEM -> Material.IRON_BLOCK
            EntityType.WITHER -> Material.WITHER_SKELETON_SKULL
            EntityType.ENDER_DRAGON -> Material.DRAGON_HEAD
            EntityType.MUSHROOM_COW -> Material.MOOSHROOM_SPAWN_EGG
            else -> Material.valueOf(entity.name + "_SPAWN_EGG")
        }
    ) {
        meta {
            name = text(formatMob(entity))
            addLore {
                +" "
                addComponent(
                    if (isKilled(entity)) {
                        translatable("$id.killed")
                    } else {
                        translatable("$id.not_killed")
                    }
                )
            }
            if (isKilled(entity)) {
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
            name = translatable("$id.filter_item.name")
            addLore {
                addComponent(
                    translatable(
                        "$id.filter_item.lore",
                        listOf(
                            text(
                                filter.first.name,
                                TextColor.color(
                                    (if (filter.first == Filter.ALL) KColors.PURPLE else if (filter.first == Filter.KILLED) KColors.GREEN else KColors.RED).color.rgb
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
                )
            }
        }
    }

    private fun resetItem() = itemStack(Material.BARRIER) {
        meta {
            name = translatable("$id.reset_item.name")
            addLore {
                addComponent(translatable("$id.reset_item.lore"))
            }
        }
    }

    private fun formatMob(entity: EntityType): String {
        return entity.name.lowercase().replace('_', ' ').split(' ').joinToString(" ", transform = { s ->
            s.replaceFirstChar { it.titlecase(Locale.getDefault()) }
        })
    }

    private fun randomMob(): EntityType {
        if (!isWon()) {
            return mobs.filter { !isKilled(it) }.random()
        } else {
            error("Goal already finished")
        }
    }

    private fun isWon() = mobs.none { !isKilled(it) }

    private fun collected(key: String, replacements: List<Component> = listOf(), markCollected: Boolean = true) {
        onlinePlayers.forEach {
            it.sendPrefixMessage(
                translatable(key, replacements)
            )
        }
        Timer.additionalInfo.clear()
        if (markCollected)
            allMobs = ArrayList(allMobs.plus(nextMob))
        if (isWon()) {
            Config.allMobsDataConfig.setSetting("nextMob", null)
            win()
        } else {
            nextMob = randomMob()
            Timer.additionalInfo.add("kill ${formatMob(nextMob)}")
        }
    }

    private fun isKilled(entity: EntityType) = allMobs.contains(entity)

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (event.entity.type == nextMob &&
            event.entity.killer != null &&
            event.entity.killer!!.isPlaying()
        ) {
            collected(
                "$id.collected_killed",
                listOf(event.entity.killer!!.name(), text(formatMob(nextMob)))
            )
        }
    }
}
