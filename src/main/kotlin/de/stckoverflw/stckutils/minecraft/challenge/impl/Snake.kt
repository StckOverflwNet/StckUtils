package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.getKey
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.user.goBackItem
import de.stckoverflw.stckutils.user.placeHolderItemGray
import de.stckoverflw.stckutils.user.placeHolderItemWhite
import de.stckoverflw.stckutils.user.settingsGUI
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.main.KSpigotMainInstance
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object Snake : Challenge() {

    private val materials = ArrayList(
        Material.values().filter { material ->
            material.name.lowercase().contains("concrete") && !material.name.lowercase().contains("powder") && material != Material.WHITE_CONCRETE
        }
    )
    private val playerMaterials = HashMap<Player, Material>()
    private val temporaryBlocks = HashMap<Player, LinkedList<Block>>()
    private var isVisible
        get() = (Config.gameChangeConfig.getSetting(id, "isVisible") ?: true) as Boolean
        set(value) = Config.gameChangeConfig.setSetting(id, "isVisible", value)
    private var isBreakable
        get() = (Config.gameChangeConfig.getSetting(id, "isBreakable") ?: true) as Boolean
        set(value) = Config.gameChangeConfig.setSetting(id, "isBreakable", value)
    private var isColored: Boolean
        get() = (Config.gameChangeConfig.getSetting(id, "isColored") ?: true) as Boolean
        set(value) = Config.gameChangeConfig.setSetting(id, "isColored", value)

    override val id: String = "snake"
    override val name: String = "§aSnake"
    override val material: Material = Material.PINK_CONCRETE
    override val description: List<String> = listOf(
        " ",
        "§7A line follows you. If you touch it you lose",
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

            button(Slots.RowThreeSlotThree, visibleItem()) {
                it.bukkitEvent.isCancelled = true
                isVisible = !isVisible
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, visibleItem())
            }

            button(Slots.RowThreeSlotFive, breakableItem()) {
                it.bukkitEvent.isCancelled = true
                isBreakable = !isBreakable
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, breakableItem())
            }

            button(Slots.RowThreeSlotSeven, colorsItem()) {
                it.bukkitEvent.isCancelled = true
                isColored = !isColored
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, colorsItem())
            }
        }
    }

    private fun visibleItem() = itemStack(Material.WHITE_BANNER) {
        meta {
            name = "§aVisible Lines"
            addLore {
                +" "
                +"§7Toggle if the lines should be visible or not"
                +"§7Lines are currently ".plus(if (isVisible) "§avisible" else "§cinvisible")
            }
        }
    }

    private fun breakableItem() = itemStack(Material.WOODEN_PICKAXE) {
        meta {
            name = "§aBreakable Lines"
            addLore {
                +" "
                +"§7Toggle if the lines should be breakable or not"
                +"§7(The temporary (white) Blocks aren't breakable)"
                +"§7Lines are currently ".plus(if (isBreakable) "§abreakable" else "§cunbreakable")
            }
        }
    }

    private fun colorsItem() = itemStack(Material.PINK_CONCRETE) {
        meta {
            name = "§aColored Lines"
            addLore {
                +" "
                +"§7Toggle if the lines should be differently colored"
                +"§7(Every Player has its own color - limits maximum players due to lack of colors)"
                +"§7Lines are currently ".plus(if (isColored) "§adifferent colors" else "§cthe same color")
            }
        }
    }

    override fun prepareChallenge() {
        if (isColored) {
            onlinePlayers.forEach { player ->
                if (playerMaterials.containsKey(player)) return
                if (materials.isEmpty()) {
                    player.gameMode = GameMode.SPECTATOR
                    player.sendMessage(StckUtilsPlugin.prefix + "§cthere was no material left for you, so you were excluded from the challenge")
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
        if (!event.player.isPlaying()) return
        if (!event.hasChangedBlock()) return
        var block = event.to.block
        while (!block.isSolid || block.type.hardness <= 0.2) {
            block = block.getRelative(BlockFace.DOWN)
        }

        if (block.hasMetadata("snake")) {
            if (isColored) {
                if (playerMaterials.containsValue(block.type)) {
                    val player = playerMaterials.getKey(block.type) as Player
                    lose(
                        "${event.player.name} touched " +
                                player.name +
                                if (player.name.endsWith('s') || player.name.endsWith('x'))
                                    "' snake trail."
                                else
                                    "'s snake trail."
                    )
                    return
                }
            }
            lose(
                "${event.player.name} touched a snake trail."
            )
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
