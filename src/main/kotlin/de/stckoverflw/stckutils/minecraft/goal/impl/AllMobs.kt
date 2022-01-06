package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.minecraft.goal.TeamGoal
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.placeHolderItemGray
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.items.*
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

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
            EntityType.ARMOR_STAND -> false
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
        title = GoalManager.translationsProvider.translate(
            nameKey,
            locale,
            id
        )
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)

            val compound = createRectCompound<EntityType>(
                Slots.RowTwoSlotTwo, Slots.RowFourSlotEight,
                iconGenerator = {
                    generateAllMobsItem(it, locale)
                }, onClick = { clickEvent, _ ->
                clickEvent.bukkitEvent.isCancelled = true
            }
            )

            // Reset Item
            button(Slots.RowTwoSlotNine, resetItem(locale)) {
                allMobs = listOf()
                nextMob = randomMob()
                it.guiInstance.reloadCurrentPage()
                onlinePlayers.forEach { player ->
                    player.sendMessage(
                        GoalManager.translationsProvider.translateWithPrefix(
                            "reset_progress",
                            player.language,
                            id,
                            arrayOf(it.bukkitEvent.whoClicked.name)
                        )
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

                clickEvent.guiInstance[Slots.RowFourSlotNine] = filterItem(filter[player.uniqueId]!!, locale)
            })
            if (!isWon()) {
                button(Slots.RowThreeSlotOne, skipItem(locale), onClick = { clickEvent ->
                    if (clickEvent.bukkitEvent.isLeftClick) {
                        collected(
                            "skipped",
                            arrayOf(clickEvent.player.name, formatMob(nextMob)),
                            false
                        )
                    } else if (clickEvent.bukkitEvent.isRightClick) {
                        collected(
                            "marked",
                            arrayOf(clickEvent.player.name, formatMob(nextMob)),
                            false
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

    private fun skipItem(locale: Locale) = itemStack(Material.BEDROCK) {
        meta {
            name = GoalManager.translationsProvider.translate(
                "skip_item.name",
                locale,
                id,
                arrayOf(formatMob(nextMob))
            )
            addLore {
                GoalManager.translationsProvider.translate(
                    "skip_item.lore",
                    locale,
                    id,
                    arrayOf(formatMob(nextMob))
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }

    private fun generateAllMobsItem(entity: EntityType, locale: Locale) = itemStack(
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
            name = "§7${formatMob(entity)}"
            addLore {
                +" "
                +if (isKilled(entity)) {
                    GoalManager.translationsProvider.translate(
                        "killed",
                        locale,
                        id
                    )
                } else {
                    GoalManager.translationsProvider.translate(
                        "not_killed",
                        locale,
                        id
                    )
                }
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

    private fun filterItem(filter: Pair<Filter, Filter>, locale: Locale) = itemStack(Material.HOPPER) {
        meta {
            name = GoalManager.translationsProvider.translate(
                "filter_item.name",
                locale,
                id
            )
            addLore {
                GoalManager.translationsProvider.translate(
                    "filter_item.lore",
                    locale,
                    id,
                    arrayOf(
                        (if (filter.first == Filter.ALL) "§d" else if (filter.first == Filter.KILLED) "§a" else "§c")
                            .plus(filter.first.name),
                        (if (filter.second == Filter.ASCENDING) "§a" else "§c").plus(filter.second.name)
                    )
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }

    private fun resetItem(locale: Locale) = itemStack(Material.BARRIER) {
        meta {
            name = GoalManager.translationsProvider.translate(
                "reset_item.name",
                locale,
                id
            )
            addLore {
                GoalManager.translationsProvider.translate(
                    "reset_item.lore",
                    locale,
                    id
                ).split("\n").forEach {
                    +it
                }
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

    private fun collected(key: String, replacements: Array<Any?> = arrayOf(), markCollected: Boolean = true) {
        onlinePlayers.forEach {
            it.sendMessage(
                GoalManager.translationsProvider.translateWithPrefix(
                    key,
                    it.language,
                    id,
                    replacements
                )
            )
        }
        Timer.additionalInfo.clear()
        if (markCollected)
            allMobs = ArrayList(allMobs.plus(nextMob))
        if (isWon()) {
            Config.allMobsDataConfig.setSetting("nextMob", null)
            win(id)
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
                "collected_killed",
                arrayOf(event.entity.killer!!.name, formatMob(nextMob))
            )
        }
    }
}
