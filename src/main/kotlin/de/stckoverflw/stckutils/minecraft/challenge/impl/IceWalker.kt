package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.BlockPos
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.extensions.geometry.LocationArea
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.structures.fillBlocks
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

object IceWalker : Challenge() {

    private var iceWalkerPlayers: List<UUID>
        get() {
            val list: MutableList<*>? = Config.challengeDataConfig.getSettingList(id, "iceWalkerPlayers")
            return list?.filterNotNull()?.map { UUID.fromString(it as String?) } ?: emptyList()
        }
        set(value) = Config.challengeDataConfig.setSetting(id, "iceWalkerPlayers", value.map { it.toString() })

    private val breakingBlocks = HashMap<Block, Int>()
    private val blockIds = HashMap<Block, Int>()
    private val brokenBlocks = ArrayList<Block>()
    private val fillMaterial = Material.PACKED_ICE
    private val referenceNano = System.nanoTime()

    override val id: String = "ice-walker"
    override val name: String = "§bIceWalker"
    override val material: Material = fillMaterial
    override val description: List<String> = listOf(
        " ",
        "§7When you walk, a 3x3 ice platform",
        "§7will be formed below you.",
        "§7You can toggle this ability by sneaking."
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null

    override fun update() {
        sendBlockBreakAnimations()
    }

    private fun sendBlockBreakAnimations() {
        // ClientboundBlockDestructionPacket(entityId, blockPos, progress)
        // entityId: has to be unique, can be random generated
        // blockPos: position of the block
        // progress: 0-9

        val toRemove = ArrayList<Block>()
        breakingBlocks.forEach {
            if (it.value > 8) {
                sendBlockBreak(blockIds[it.key]!!, it.key.location, 0)
                brokenBlocks.add(it.key)
                toRemove.add(it.key)
                return@forEach
            } else if (onlinePlayers.any { player ->
                val locationArea = LocationArea(
                        player.location.toBlockLocation().add(-2.0, -1.0, -1.0),
                        player.location.toBlockLocation().add(2.0, -1.0, 1.0)
                    )
                val locationArea2 = LocationArea(
                        player.location.toBlockLocation().add(-1.0, -1.0, -2.0),
                        player.location.toBlockLocation().add(1.0, -1.0, 2.0)
                    )

                player.isPlaying() &&
                    iceWalkerPlayers.contains(player.uniqueId) &&
                    locationArea.fillBlocks.plus(locationArea2.fillBlocks).distinct().contains(it.key)
            }
            ) {
                breakingBlocks[it.key] = 0
            } else {
                breakingBlocks[it.key] = it.value + 1
            }
            sendBlockBreak(blockIds[it.key]!!, it.key.location, it.value)
        }
        toRemove.forEach {
            sendBlockBreak(blockIds[it]!!, it.location, 0)
            breakingBlocks.remove(it)
        }
    }

    private fun sendBlockBreak(id: Int, location: Location, value: Int) {
        val packet = net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket(
            id,
            BlockPos(location),
            value
        )

        onlinePlayers.forEach { player ->
            if (player.location.toBlockLocation().distance(location) <= 25) {
                (player as CraftPlayer).handle.connection.send(packet)
            }
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val toRemove = ArrayList<Block>()
        brokenBlocks.forEach {
            it.type = Material.AIR
            toRemove.add(it)
        }
        toRemove.forEach {
            brokenBlocks.remove(it)
        }
        if (!event.hasChangedBlock() ||
            !event.player.isPlaying()
        ) {
            return
        }
        if (event.player.location.toBlockLocation().add(0.0, -1.0, 0.0).block.type == fillMaterial) {
            event.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20, 1, false, false, false))
        }
        if (!iceWalkerPlayers.contains(event.player.uniqueId)
        ) {
            return
        }
        val locationArea = LocationArea(
            event.player.location.toBlockLocation().add(-2.0, -1.0, -1.0),
            event.player.location.toBlockLocation().add(2.0, -1.0, 1.0)
        )
        val locationArea2 = LocationArea(
            event.player.location.toBlockLocation().add(-1.0, -1.0, -2.0),
            event.player.location.toBlockLocation().add(1.0, -1.0, 2.0)
        )
        locationArea.fillBlocks.plus(locationArea2.fillBlocks).distinct().forEach {
            it.type = fillMaterial
            breakingBlocks[it] = 0
            if (!blockIds.contains(it)) {
                blockIds[it] = (System.nanoTime() - referenceNano).toInt()
            }
            sendBlockBreak(blockIds[it]!!, it.location, 0)
        }
    }

    @EventHandler
    fun onSneak(event: PlayerToggleSneakEvent) {
        if (event.isSneaking ||
            !event.player.isPlaying()
        ) {
            return
        }
        iceWalkerPlayers = if (iceWalkerPlayers.contains(event.player.uniqueId)) {
            iceWalkerPlayers.minus(event.player.uniqueId)
        } else {
            iceWalkerPlayers.plus(event.player.uniqueId)
        }
    }
}
