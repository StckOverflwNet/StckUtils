package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.runnables.sync
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

object ChunkSync : Challenge() {
    override val id: String = "chunk-sync"
    override val name: String = "§9Chunk Sync"
    override val material: Material = Material.HEART_OF_THE_SEA
    override val description: List<String> = listOf(
        " ",
        "§7Every Block that you break or place",
        "§7is gonna place/break at the same location",
        "§7in every Chunk"
    )

    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    private val blockActions = HashMap<Block, ArrayList<Chunk>>()

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        blockActions[event.blockPlaced] = arrayListOf()
        runInLoadedChunks()
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        blockActions[event.block] = arrayListOf()
        runInLoadedChunks()
    }

    private fun runInLoadedChunks() {
        onlinePlayers.forEach {
            around(it.chunk, 3).forEach { chunk ->
                blockActions.forEach { (block, chunks) ->
                    if (!chunks.contains(chunk)) {
                        val location: Location = if (chunk.x != 0 && chunk.z != 0) {
                            block.location.clone().add(
                                (block.location.blockX * chunk.x).toDouble(),
                                0.0,
                                (block.location.blockZ * chunk.z).toDouble()
                            )
                        } else if (chunk.x != 0) {
                            block.location.clone().set(
                                (block.location.blockX * chunk.x).toDouble(),
                                block.location.blockY.toDouble(),
                                (block.location.blockZ).toDouble()
                            )
                        } else if (chunk.z != 0) {
                            block.location.clone().set(
                                (block.location.blockX).toDouble(),
                                block.location.blockY.toDouble(),
                                (block.location.blockZ * chunk.z).toDouble()
                            )
                        } else {
                            block.location.clone().set(
                                (block.location.blockX).toDouble(),
                                block.location.blockY.toDouble(),
                                (block.location.blockZ).toDouble()
                            )
                        }
                        sync {
                            location.block.setType(block.type, true)
                        }
                        chunks.add(chunk)
                    }
                }
            }
        }
    }

    private fun around(origin: Chunk, radius: Int): Collection<Chunk> {
        val world = origin.world
        val length = radius * 2 + 1
        val chunks: MutableSet<Chunk> = HashSet(length * length)
        val cX = origin.x
        val cZ = origin.z

        for (x in -radius..radius) {
            for (z in -radius..radius) {
                chunks.add(world.getChunkAt(cX + x, cZ + z))
            }
        }

        return chunks
    }
}
