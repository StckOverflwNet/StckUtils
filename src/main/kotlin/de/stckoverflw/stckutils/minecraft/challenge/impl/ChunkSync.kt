package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Bukkit.getServer
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import java.util.*

object ChunkSync : Challenge() {

    private val blockActions = HashMap<Block, ArrayList<Chunk>>()

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

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        // blockActions[event.blockPlaced] = arrayListOf()
        placeBlocks(event.block, getChunks(event.player))
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        // blockActions[event.block] = arrayListOf()
        placeBlocks(event.block, getChunks(event.player))
    }

    private fun placeBlocks(block: Block, chunks: List<Chunk>) {

        val newLocations = arrayListOf(
            Location(
                block.world,
                block.location.clone().x + 16,
                block.location.clone().y,
                block.location.clone().z,
            ),
            Location(
                block.world,
                block.location.clone().x - 16,
                block.location.clone().y,
                block.location.clone().z,
            ),
            Location(
                block.world,
                block.location.clone().x,
                block.location.clone().y,
                block.location.clone().z + 16,
            ),
            Location(
                block.world,
                block.location.clone().x,
                block.location.clone().y,
                block.location.clone().z - 16,
            )
        )
        val placedChunks = ArrayList<Chunk>()
        newLocations.forEach {
            if (it.chunk.isLoaded) {
                if (it.block != block) {
                    if (!placedChunks.contains(it.chunk)) {
                        placedChunks.add(it.chunk)
                        onlinePlayers.forEach { player ->
                            player.sendBlockChange(it, block.blockData)
                        }
                    }
                }
            }
        }
        val chunksWithoutPlaced = chunks.filter { chunk -> !placedChunks.contains(chunk) }
        if (chunksWithoutPlaced.isNotEmpty()) {
            placeBlocks(block, chunksWithoutPlaced)
        }
    }

    fun getChunks(entity: Player): ArrayList<Chunk> {
        val chunks = ArrayList<Chunk>()
        val c: Chunk = entity.world.getChunkAt(entity.location)
        val renderDistance = getServer().viewDistance
        for (x in c.x - renderDistance..c.x + renderDistance) {
            for (z in c.z - renderDistance..c.z + renderDistance) {
                if (!chunks.contains(entity.world.getChunkAt(x, z))) {
                    chunks.add(entity.world.getChunkAt(x, z))
                }
            }
        }
        return chunks
    }
}
