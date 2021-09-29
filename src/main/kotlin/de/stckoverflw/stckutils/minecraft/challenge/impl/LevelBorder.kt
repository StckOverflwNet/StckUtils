package de.stckoverflw.stckutils.minecraft.challenge.impl

import com.comphenix.protocol.PacketType
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isInArea
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.goBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.main.KSpigotMainInstance
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLevelChangeEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.lang.reflect.InvocationTargetException

object LevelBorder : Challenge() {

    private var xpLevel: Int
        get() = (Config.challengeDataConfig.getSetting(id, "xpLevel") ?: 0) as Int
        set(value) = Config.challengeDataConfig.setSetting(id, "xpLevel", value)
    private var xpProgress: Double
        get() = (Config.challengeDataConfig.getSetting(id, "xpProgress") ?: 0.0) as Double
        set(value) = Config.challengeDataConfig.setSetting(id, "xpProgress", value)
    private var isFirstRun
        get() = (Config.challengeDataConfig.getSetting(MobDuplicator.id, "isFirstRun") ?: true) as Boolean
        set(value) = Config.challengeDataConfig.setSetting(MobDuplicator.id, "isFirstRun", value)

    override val id: String = "level-border"
    override val name: String = "§eLevel Border"
    override val material: Material = Material.EXPERIENCE_BOTTLE
    override val description: List<String> = listOf(
        " ",
        "§7You start with a world border of 1.",
        "§7It will increase and decrease",
        "§7as your xp level changes."
    )
    override val usesEvents: Boolean = true

    private fun borderCenter(player: Player, location: Location) {
        val packet = StckUtilsPlugin.protocolManager!!.createPacket(PacketType.Play.Server.SET_BORDER_CENTER)

        packet.doubles
            .write(0, location.blockX.toDouble())
            .write(1, location.blockY.toDouble())

        try {
            StckUtilsPlugin.protocolManager!!.sendServerPacket(player, packet)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot send packet $packet", e)
        }
    }

    private fun border(player: Player, size: Double) {
        val packet = StckUtilsPlugin.protocolManager!!.createPacket(PacketType.Play.Server.SET_BORDER_SIZE)

        packet.doubles
            .write(0, size)

        try {
            StckUtilsPlugin.protocolManager!!.sendServerPacket(player, packet)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot send packet $packet", e)
        }
    }

    private fun moveBorder(player: Player, fromSize: Double, toSize: Double, time: Long) {
        val packet = StckUtilsPlugin.protocolManager!!.createPacket(PacketType.Play.Server.SET_BORDER_LERP_SIZE)

        packet.doubles
            .write(0, fromSize)
            .write(1, toSize)

        packet.longs
            .write(0, time)

        try {
            StckUtilsPlugin.protocolManager!!.sendServerPacket(player, packet)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot send packet $packet", e)
        }
    }

    private fun resetBorder(player: Player) {
        border(player, 30000000.0)
    }

    override fun onToggle() {
        if (!active) {
            onlinePlayers.forEach {
                resetBorder(it)
            }
        } else {
            val overWorld = KSpigotMainInstance.server.worlds.first {
                it.environment == World.Environment.NORMAL
            }
            onlinePlayers.forEach { player ->
                player.teleportAsync(overWorld.spawnLocation)
                if (isFirstRun) {
                    player.level = 0
                    player.exp = 0F
                }
                border(player, (xpLevel + 1).toDouble())
            }
            isFirstRun = false
        }
    }

    override fun onTimerToggle() {
        if (!Timer.running) {
            onlinePlayers.forEach { player ->
                resetBorder(player)
            }
        } else {
            onlinePlayers.forEach { player ->
                player.level = xpLevel
                player.exp = xpProgress.toFloat()
                if (!player.isInArea(player.world.spawnLocation, xpLevel.toDouble())) {
                    player.teleportAsync(player.world.spawnLocation)
                }
                border(player, (xpLevel + 1).toDouble())
            }
        }
    }

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

            button(Slots.RowThreeSlotFive, resetItem()) {
                it.bukkitEvent.isCancelled = true
                isFirstRun = true
                xpLevel = 0
                xpProgress = 0.0
                onToggle()
            }
        }
    }

    private fun resetItem() = itemStack(Material.BARRIER) {
        meta {
            name = "§4Reset"
            addLore {
                +" "
                +"§7Reset the Border and Level"
            }
        }
    }

    @EventHandler
    fun onXpProgress(event: PlayerExpChangeEvent) {
        xpProgress = event.player.exp.toDouble()
        xpLevel = event.player.level
        onlinePlayers.forEach {
            it.exp = xpProgress.toFloat()
            it.level = xpLevel
        }
    }

    @EventHandler
    fun onXpLevel(event: PlayerLevelChangeEvent) {
        xpProgress = event.player.exp.toDouble()
        xpLevel = event.player.level
        onlinePlayers.forEach {
            it.exp = xpProgress.toFloat()
            it.level = xpLevel
            moveBorder(it, (xpLevel + 1).toDouble(), (xpLevel + 2).toDouble(), 1000L)
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        xpLevel = event.newLevel
        xpProgress = 0.0
        event.droppedExp = 0
        event.newExp = 0
        onlinePlayers.forEach {
            it.exp = xpProgress.toFloat()
            it.level = xpLevel
            moveBorder(it, (xpLevel + 1).toDouble(), (xpLevel + 2).toDouble(), 1000L)
        }
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        event.player.level = xpLevel
        event.player.exp = 0F
        event.player.teleportAsync(event.player.world.spawnLocation)
        border(event.player, (xpLevel + 1).toDouble())
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.level = xpLevel
        event.player.exp = xpProgress.toFloat()
        if (!event.player.isInArea(event.player.world.spawnLocation, xpLevel.toDouble())) {
            event.player.teleportAsync(event.player.world.spawnLocation)
        }
        border(event.player, (xpLevel + 1).toDouble())
    }
}
