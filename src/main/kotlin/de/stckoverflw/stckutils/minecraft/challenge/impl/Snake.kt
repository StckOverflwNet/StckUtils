package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.coloredString
import de.stckoverflw.stckutils.extension.errorTranslatable
import de.stckoverflw.stckutils.extension.getKey
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.extension.sendPrefixMessage
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.util.GUIPage
import de.stckoverflw.stckutils.util.getGoBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.extensions.onlinePlayers
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
import net.axay.kspigot.main.KSpigotMainInstance
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.LinkedList
import java.util.Locale

object Snake : Challenge() {

    private val materials = ArrayList(
        Material.values().filter { material ->
            material.name.lowercase().contains("concrete") && !material.name.lowercase()
                .contains("powder") && material != Material.WHITE_CONCRETE
        }
    )
    private val playerMaterials = HashMap<Player, Material>()
    private val temporaryBlocks = HashMap<Player, LinkedList<Block>>()
    private var isVisible
        get() = (Config.challengeConfig.getSetting(id, "isVisible") ?: true) as Boolean
        set(value) = Config.challengeConfig.setSetting(id, "isVisible", value)
    private var isBreakable
        get() = (Config.challengeConfig.getSetting(id, "isBreakable") ?: true) as Boolean
        set(value) = Config.challengeConfig.setSetting(id, "isBreakable", value)
    private var isColored: Boolean
        get() = (Config.challengeConfig.getSetting(id, "isColored") ?: true) as Boolean
        set(value) = Config.challengeConfig.setSetting(id, "isColored", value)

    override val id: String = "snake"
    override val material: Material = Material.PINK_CONCRETE
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = translatable(nameKey).coloredString(locale)
        defaultPage = 0
        page(0) {
            placeholder(Slots.Border, placeHolderItemGray)
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            button(
                Slots.RowThreeSlotOne,
                getGoBackItem(locale)
            ) {
                it.player.openGUI(settingsGUI(locale), GUIPage.challengesPageNumber)
            }

            button(
                Slots.RowThreeSlotThree,
                visibleItem(locale)
            ) {
                it.bukkitEvent.isCancelled = true
                isVisible = !isVisible
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, visibleItem(locale))
            }

            button(
                Slots.RowThreeSlotFive,
                breakableItem(locale)
            ) {
                it.bukkitEvent.isCancelled = true
                isBreakable = !isBreakable
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, breakableItem(locale))
            }

            button(
                Slots.RowThreeSlotSeven,
                colorsItem(locale)
            ) {
                it.bukkitEvent.isCancelled = true
                isColored = !isColored
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, colorsItem(locale))
            }
        }
    }

    private fun visibleItem(locale: Locale) = itemStack(Material.WHITE_BANNER) {
        meta {
            name = translatable("$id.visible_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.visible_item.lore")
                        .args(
                            if (isVisible) {
                                translatable("$id.visible", TextColor.color(Color.GREEN.asRGB()))
                            } else {
                                translatable("$id.invisible", TextColor.color(Color.RED.asRGB()))
                            }
                        )
                        .render(locale)
                )
            }
        }
    }

    private fun breakableItem(locale: Locale) = itemStack(Material.WOODEN_PICKAXE) {
        meta {
            name = translatable("$id.breakable_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.breakable_item.lore")
                        .args(
                            if (isBreakable) {
                                translatable("$id.breakable", TextColor.color(Color.GREEN.asRGB()))
                            } else {
                                translatable("$id.unbreakable", TextColor.color(Color.RED.asRGB()))
                            }
                        )
                        .render(locale)
                )
            }
        }
    }

    private fun colorsItem(locale: Locale) = itemStack(Material.PINK_CONCRETE) {
        meta {
            name = translatable("$id.colors_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.colors_item.lore")
                        .args(
                            if (isColored) {
                                translatable("$id.different_colors", TextColor.color(Color.GREEN.asRGB()))
                            } else {
                                translatable("$id.same_colors", TextColor.color(Color.RED.asRGB()))
                            }
                        )
                        .render(locale)
                )
            }
        }
    }

    override fun prepareChallenge() {
        if (isColored) {
            onlinePlayers.forEach { player ->
                if (playerMaterials.containsKey(player)) return
                if (materials.isEmpty()) {
                    player.gameMode = GameMode.SPECTATOR
                    player.sendPrefixMessage(
                        errorTranslatable("$id.no_material_left")
                    )
                }
                val material = materials.random()
                playerMaterials[player] = material
                materials.remove(material)
                temporaryBlocks[player] = LinkedList()
            }
        }
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        if (!event.player.isPlaying()) return
        if (!isBreakable && event.block.hasMetadata("snake") || event.block.hasMetadata("temporary_snake"))
            event.isCancelled = true
        if (event.block.hasMetadata("snake")) {
            event.isDropItems = false
            event.expToDrop = 0
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.player.isPlaying()) {
            return
        }
        if (!event.hasChangedBlock()) {
            return
        }
        var block = event.to.block
        while (!block.isSolid || block.type.hardness <= 0.2) {
            block = block.getRelative(BlockFace.DOWN)
        }

        if (block.hasMetadata("snake")) {
            if (isColored) {
                if (playerMaterials.containsValue(block.type)) {
                    val player = playerMaterials.getKey(block.type) as Player
                    lose(
                        listOf(
                            event.player.name(),
                            player.name()
                                .append(
                                    text(
                                        if (player.name.endsWith('s') || player.name.endsWith('x')) {
                                            "'"
                                        } else {
                                            "'s"
                                        }
                                    )
                                )
                        )
                    )
                    return
                }
            }
            lose(listOf(event.player.name(), text("a")))
            return
        }

        if (temporaryBlocks[event.player]?.contains(block) == true) return

        if (block.boundingBox.height < 0.85 && isVisible)
            block = block.getRelative(BlockFace.DOWN)

        temporaryBlocks[event.player]!!.add(block)
        if (temporaryBlocks[event.player]!!.size > 2) {
            val tempBlock = temporaryBlocks[event.player]!!.poll()
            if (isVisible) {
                if (isColored)
                    tempBlock.type = playerMaterials[event.player]!!
                else
                    tempBlock.type = Material.PINK_CONCRETE
            }
            tempBlock.setMetadata("snake", FixedMetadataValue(KSpigotMainInstance, true))
            tempBlock.removeMetadata("temporary_snake", KSpigotMainInstance)
        }

        if (isVisible)
            block.type = Material.WHITE_CONCRETE
        block.setMetadata("temporary_snake", FixedMetadataValue(KSpigotMainInstance, true))
        block.getRelative(BlockFace.UP).type = Material.AIR
    }
}
