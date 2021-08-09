package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.user.goBackItem
import de.stckoverflw.stckutils.user.placeHolderItemGray
import de.stckoverflw.stckutils.user.placeHolderItemWhite
import de.stckoverflw.stckutils.user.settingsGUI
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.main.KSpigotMainInstance
import net.axay.kspigot.runnables.sync
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

object ChunkFlattener : Challenge() {
    override val id: String = "chunk-flattener"
    override val name: String = "§aChunkFlattener"
    override val material: Material = Material.BEDROCK
    override val description: List<String> = listOf(
        " ",
        "§7Every now and then the top layer",
        "§7of your current chunk will be removed"
    )
    override val usesEvents: Boolean = false

    override fun configurationGUI(): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = name
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, goBackItem) { it.player.openGUI(settingsGUI(), 2) }

            button(Slots.RowThreeSlotSeven, plusItem()) {
                it.bukkitEvent.isCancelled = true
                handleUpdateClick(it.bukkitEvent, true)
                updateInventory(it.bukkitEvent.inventory)
            }

            button(Slots.RowThreeSlotSix, resetItem()) {
                it.bukkitEvent.isCancelled = true
                period = 10
                updateInventory(it.bukkitEvent.inventory)
            }

            button(Slots.RowThreeSlotFive, minusItem()) {
                it.bukkitEvent.isCancelled = true
                handleUpdateClick(it.bukkitEvent, false)
                updateInventory(it.bukkitEvent.inventory)
            }

            button(Slots.RowThreeSlotThree, dropItem()) {
                it.bukkitEvent.isCancelled = true
                doDrop = !doDrop
                updateInventory(it.bukkitEvent.inventory)
            }
        }
    }

    private fun updateInventory(inv: Inventory) {
        inv.setItem(20, dropItem())
        inv.setItem(22, minusItem())
        inv.setItem(23, resetItem())
        inv.setItem(24, plusItem())
    }

    private fun dropItem() = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = "§aDrop Items"
            addLore {
                +" "
                +"§7Toggle Item Drops"
                +"§7Value: §f${doDrop}"
            }
        }
    }

    private fun resetItem() = itemStack(Material.BARRIER) {
        meta {
            name = "§4Reset"
            addLore {
                +" "
                +"§7Reset the period of the ChunkFlattener"
                +"§7Period: §f${period}§7s (§8Default: 10§7)"
            }
        }
    }

    private fun plusItem() = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = "§aPlus"
            addLore {
                +" "
                +"§7Increase the period of the ChunkFlattener"
                +"§7Period: §f${period}§7s"
                +" "
                +"§7Left-click:             §a+ 1§7s"
                +"§7Right-click:            §a+ 10§7s"
            }
        }
    }

    private fun minusItem() = itemStack(Material.POLISHED_BLACKSTONE_BUTTON) {
        meta {
            name = "§cMinus"
            addLore {
                +" "
                +"§7Decrease the period of the ChunkFlattener"
                +"§7Period: §f${period}§7s"
                +" "
                +"§7Left-click:             §c- 1§7s"
                +"§7Right-click:            §c- 10§7s"
            }
        }
    }

    private fun handleUpdateClick(event: InventoryClickEvent, isPositive: Boolean) {
        if (event.isLeftClick) {
            updateMultiplier(if (isPositive) 1 else -1)
            return
        } else if (event.isRightClick) {
            updateMultiplier(if (isPositive) 10 else -10)
            return
        }
    }

    private fun updateMultiplier(value: Int) {
        if ((period + value) < maxPeriod && (period + value) > minPeriod) {
            period += value
        } else {
            period = if (value > 0) maxPeriod else minPeriod
        }
    }

    private var minPeriod: Int = 1
    private var maxPeriod: Int = 60
    private var period: Int = 10
    private var doDrop: Boolean = false
    private var time: Int = 0

    override fun update() {
        time++
        if (time % period != 0) return

        KSpigotMainInstance.server.onlinePlayers.forEach { player ->
            if (player.gameMode == GameMode.SURVIVAL) {
                for (i in 0..15) {
                    for (j in 0..15) {
                        sync {
                            val block = player.world.getHighestBlockAt(player.location.chunk.x * 16 + i, player.location.chunk.z * 16 + j)
                            if (doDrop) {
                                block.breakNaturally()
                            } else {
                                val blocks = ArrayList<Block>()
                                blocks.add(block)
                                while (blocks[blocks.lastIndex].getRelative(BlockFace.UP).type != Material.AIR) {
                                    blocks.add(blocks[blocks.lastIndex].getRelative(BlockFace.UP))
                                }
                                blocks.reverse()
                                blocks.forEach {
                                    player.world.playEffect(it.location, Effect.STEP_SOUND, it.type)
                                    player.world.playSound(it.location, it.soundGroup.breakSound, 1F, 1F)
                                    it.type = Material.AIR
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}