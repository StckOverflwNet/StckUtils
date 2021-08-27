package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.main.KSpigotMainInstance
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object Snake : Challenge() {
    override val id: String = "snake"
    override val name: String = "§aSnake"
    override val material: Material = Material.PINK_CONCRETE
    override val description: List<String> = listOf(
        " ",
        "§7A line follows you. If you touch it you lose",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    private val materials = ArrayList<Material>(
        Material.values().filter { material ->
            material.name.lowercase().contains("concrete") && !material.name.lowercase().contains("powder") && material != Material.WHITE_CONCRETE
        })
    private val playerMaterials = HashMap<Player, Material>()
    private val temporaryBlocks = HashMap<Player, LinkedList<Block>>()

    override fun prepareChallenge() {
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

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedBlock()) return
        var block = event.to.block
        do {
            block = block.getRelative(BlockFace.DOWN)
        } while (!block.isSolid || block.type.hardness <= 0.2)

        if (block.hasMetadata("snake")) return lose("${event.player.name} touched a snake trail.")

        if (temporaryBlocks[event.player]?.contains(block) == true) return

        temporaryBlocks[event.player]!!.add(block)
        if (temporaryBlocks[event.player]!!.size > 2) {
            val tempBlock = temporaryBlocks[event.player]!!.poll()
            tempBlock.type = playerMaterials[event.player]!!
            tempBlock.setMetadata("snake", FixedMetadataValue(KSpigotMainInstance, true))
        }
        block.type = Material.WHITE_CONCRETE
    }
}
