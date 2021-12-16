package de.stckoverflw.stckutils.minecraft.goal.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.goal.TeamGoal
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.placeHolderItemGray
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.items.*
import net.axay.kspigot.main.KSpigotMainInstance
import org.bukkit.Material
import org.bukkit.advancement.Advancement
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

object AllAdvancements : TeamGoal() {

    object AdvancementIterable : Iterable<Advancement> {
        override fun iterator(): Iterator<Advancement> = KSpigotMainInstance.server.advancementIterator()
    }

    private var allAdvancements: List<Advancement>
        get() {
            val list: MutableList<*>? = Config.allAdvancementsDataConfig.getSettingList("allAdvancements")
            return if (list == null || list.isEmpty()) listOf()
            else list.filterNotNull().map { getAdvancement(it as String)!! }
        }
        set(value) = Config.allAdvancementsDataConfig.setSetting("allAdvancements", value.map { it.key.toString() })
    private val advancements: List<Advancement> = AdvancementIterable.toList().filter { it.key.key.split("/")[0] != "recipes" }
    private var nextAdvancement: Advancement
        get() {
            var advancement = Config.allAdvancementsDataConfig.getSetting("nextAdvancement")
            if (advancement == null) {
                val adv = randomAdvancement().key.toString()
                Config.allAdvancementsDataConfig.setSetting("nextAdvancement", adv)
                advancement = adv
            }
            return getAdvancement(advancement as String)!!
        }
        set(value) = Config.allAdvancementsDataConfig.setSetting("nextAdvancement", value.key.toString())
    private var formattedAdvancement = formatAdvancement(nextAdvancement)

    private var filter: HashMap<UUID, Pair<Filter, Filter>> = HashMap()

    private enum class Filter {
        DONE,
        NOT_DONE,
        ALL,
        ASCENDING,
        DESCENDING;
    }

    override val id: String = "all-advancements"
    override val name: String = "§dAll Advancements"
    override val description = listOf(
        " ",
        "§7Get all Advancements",
    )
    override val material = Material.KNOWLEDGE_BOOK

    override fun onTimerToggle() {
        if (Timer.running) {
            if (!isWon() && !Timer.additionalInfo.contains(formattedAdvancement)) {
                Timer.additionalInfo.clear()
                Timer.additionalInfo.add(formattedAdvancement)
            }
        }
    }

    fun resetFilter(player: Player) {
        filter[player.uniqueId] = Pair(Filter.ALL, Filter.ASCENDING)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun gui() = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = name
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)

            val compound = createRectCompound<Advancement>(
                Slots.RowTwoSlotTwo, Slots.RowFourSlotEight,
                iconGenerator = {
                    generateAllAdvancementsItem(it)
                }, onClick = { clickEvent, _ ->
                clickEvent.bukkitEvent.isCancelled = true
            }
            )

            // Reset Item
            button(Slots.RowTwoSlotNine, resetItem(), onClick = {
                allAdvancements = listOf()
                nextAdvancement = randomAdvancement()
                formattedAdvancement = formatAdvancement(nextAdvancement)
                it.guiInstance.reloadCurrentPage()
                broadcast(StckUtilsPlugin.prefix + "§4${it.bukkitEvent.whoClicked.name} reset the progress of All Advancements")
            })

            // Filter Button (Filter only collected/only not collected/all)
            button(Slots.RowFourSlotNine, filterItem(Pair(Filter.ALL, Filter.ASCENDING)), onClick = { clickEvent ->
                clickEvent.bukkitEvent.isCancelled = true
                val player = clickEvent.player
                if (filter[player.uniqueId] == null) resetFilter(player)
                if (clickEvent.bukkitEvent.isLeftClick) {
                    when (filter[player.uniqueId]!!.first) {
                        Filter.DONE -> {
                            filter[player.uniqueId] = filter[player.uniqueId]!!.copy(
                                first = Filter.NOT_DONE
                            )
                        }
                        Filter.NOT_DONE -> {
                            filter[player.uniqueId] = filter[player.uniqueId]!!.copy(
                                first = Filter.ALL
                            )
                        }
                        else -> {
                            filter[player.uniqueId] = filter[player.uniqueId]!!.copy(
                                first = Filter.DONE
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
                compound.sortContentBy(filter[player.uniqueId]!!.second == Filter.DESCENDING) { it.key.key }
                compound.setContent(getContent(filter[player.uniqueId]!!))
                clickEvent.guiInstance.reloadCurrentPage()

                clickEvent.guiInstance[Slots.RowFourSlotNine] = filterItem(filter[player.uniqueId]!!)
            })
            if (!isWon()) {
                button(Slots.RowThreeSlotOne, skipItem(), onClick = { clickEvent ->
                    clickEvent.bukkitEvent.isCancelled = true
                    compound.sortContentBy(filter[clickEvent.player.uniqueId]!!.second == Filter.DESCENDING) { it.key.key }
                    if (clickEvent.bukkitEvent.isLeftClick) {
                        done(
                            StckUtilsPlugin.prefix + "§a${clickEvent.player.name} skipped $formattedAdvancement",
                            false
                        )
                    } else {
                        done(
                            StckUtilsPlugin.prefix + "§a${clickEvent.player.name} marked $formattedAdvancement as collected"
                        )
                        compound.setContent(getContent(filter[clickEvent.player.uniqueId]!!))
//                        clickEvent.guiInstance.reloadCurrentPage()
                    }
                    if (!isWon()) {
                        clickEvent.guiInstance[Slots.RowThreeSlotOne] = skipItem()
                    } else {
                        clickEvent.guiInstance[Slots.RowThreeSlotOne] = placeHolderItemGray
                    }
                })
            }

            compound.addContent(getContent(Pair(Filter.ALL, Filter.ASCENDING)))
            compound.sortContentBy(false) { it.key.key }

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

    private fun getContent(filter: Pair<Filter, Filter>): List<Advancement> {
        return if (filter.second == Filter.ASCENDING) {
            when (filter.first) {
                Filter.DONE -> allAdvancements.sortedBy { it.key.toString() }
                Filter.NOT_DONE -> advancements.minus(allAdvancements.toSet()).sortedBy { it.key.toString() }
                else -> advancements.sortedBy { it.key.toString() }
            }
        } else {
            when (filter.first) {
                Filter.DONE -> allAdvancements.sortedBy { it.key.toString() }.asReversed()
                Filter.NOT_DONE -> advancements.minus(allAdvancements.toSet()).sortedBy { it.key.toString() }.asReversed()
                else -> advancements.sortedBy { it.key.toString() }.asReversed()
            }
        }
    }

    private fun skipItem(): ItemStack {
        val item = itemStack(Material.BEDROCK) {
            meta {
                name = "§eSkip current Advancement ($formattedAdvancement)"
                addLore {
                    +" "
                    +"§7LMB - Skips §f$formattedAdvancement"
                    +"§7RMB - Marks §f$formattedAdvancement§7 as done"
                }
            }
        }
        return item
    }

    private fun generateAllAdvancementsItem(advancement: Advancement): ItemStack {
        val itemStack = itemStack(
            (advancement.display?.icon()?.type) ?: Material.WRITABLE_BOOK
        ) {
            meta {
                name = "§7${formatAdvancement(advancement)}"
                addLore {
                    +" "
                    +"%description%"
                    +" "
                    +"§7Category: §d${formatAdvancementCategory(advancement)}"
                    +" "
                    +if (isDone(advancement)) "§7[§a+§7] §aDone" else "§7[§c~§7] §cNot Done"
                }
                if (isDone(advancement)) {
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

        val meta = itemStack.itemMeta
        val lore = meta.lore()
        lore!![1] = advancement.display!!.description()
        meta.lore(lore)
        itemStack.itemMeta = meta

        return itemStack
    }

    private fun filterItem(filter: Pair<Filter, Filter>) = itemStack(Material.HOPPER) {
        meta {
            name = "§8Filter"
            addLore {
                +" "
                +"§7LMB - ".plus(if (filter.first == Filter.ALL) "§d" else if (filter.first == Filter.DONE) "§a" else "§c")
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
                +"§7Reset the progress of All Advancements"
            }
        }
    }

    private fun formatAdvancementCategory(advancement: Advancement): String {
        return advancement.key.key.lowercase().replaceAfter('/', "").replace("/", "").split(' ').joinToString(" ", transform = { s ->
            s.replaceFirstChar { it.titlecase(Locale.getDefault()) }
        })
    }

    private fun formatAdvancement(advancement: Advancement): String {
        return advancement.key.key.lowercase().replaceBefore('/', "").replaceFirst("/", "").replace("/", " - ").replace('_', ' ').split(' ')
            .joinToString(" ", transform = { s ->
                s.replaceFirstChar { it.titlecase(Locale.getDefault()) }
            })
    }

    private fun randomAdvancement(): Advancement {
        if (!isWon()) {
            return advancements.minus(allAdvancements.toSet()).random()
        } else {
            error("Goal already finished")
        }
    }

    private fun isWon() = advancements.minus(allAdvancements.toSet()).isEmpty()

    private fun isDone(advancement: Advancement) = allAdvancements.contains(advancement)

    @OptIn(DelicateCoroutinesApi::class)
    private fun done(message: String, markCollected: Boolean = true) {
        broadcast(message)
        Timer.additionalInfo.clear()
        if (markCollected) {
            allAdvancements = ArrayList(allAdvancements.plus(nextAdvancement))
        }
        if (isWon()) {
            Config.allAdvancementsDataConfig.setSetting("nextAdvancement", null)
            win("You got all Advancements!")
            return
        } else {
            nextAdvancement = randomAdvancement()
            formattedAdvancement = formatAdvancement(nextAdvancement)
            Timer.additionalInfo.add(formattedAdvancement)
            GlobalScope.launch {
                onlinePlayers.forEach {
                    if (hasAdvancement(it, nextAdvancement)) {
                        done(
                            StckUtilsPlugin.prefix + "§a${it.name} already got $formattedAdvancement"
                        )
                        return@launch
                    }
                }
            }
        }
    }

    private fun hasAdvancement(player: Player, advancement: Advancement): Boolean {
        return player.getAdvancementProgress(advancement).isDone
        // returns true or false.
    }

    private fun getAdvancement(name: String): Advancement? {
        return AdvancementIterable.firstOrNull { it.key.toString() == name }
    }

    @EventHandler
    fun onAdvancementDone(event: PlayerAdvancementDoneEvent) {
        if (event.player.isPlaying() && event.advancement == nextAdvancement) {
            done(
                StckUtilsPlugin.prefix + "§a${event.player.name} got $formattedAdvancement"
            )
        }
    }
}
