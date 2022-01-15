package de.stckoverflw.stckutils.minecraft.challenge.impl

import com.comphenix.protocol.PacketType
import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.coloredString
import de.stckoverflw.stckutils.extension.isInArea
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.minecraft.timer.Timer
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
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component.translatable
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
import org.bukkit.event.player.PlayerTeleportEvent
import java.lang.reflect.InvocationTargetException
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt

object LevelBorder : Challenge(true) {

    private var xpLevel: Int
        get() = (Config.challengeDataConfig.getSetting(id, "xpLevel") ?: 0) as Int
        set(value) = Config.challengeDataConfig.setSetting(id, "xpLevel", value)
    private var xpProgress: Double
        get() = (Config.challengeDataConfig.getSetting(id, "xpProgress") ?: 0.0) as Double
        set(value) = Config.challengeDataConfig.setSetting(id, "xpProgress", value)
    private var isFirstRun
        get() = (Config.challengeDataConfig.getSetting(id, "isFirstRun") ?: true) as Boolean
        set(value) = Config.challengeDataConfig.setSetting(id, "isFirstRun", value)
    private var worldSpawn: Location?
        get() = Config.challengeDataConfig.getLocation(id, "worldSpawn")
        set(value) = Config.challengeDataConfig.setLocation(id, "worldSpawn", value!!)
    private var netherSpawn: Location?
        get() = Config.challengeDataConfig.getLocation(id, "netherSpawn")
        set(value) = Config.challengeDataConfig.setLocation(id, "netherSpawn", value!!)
    private var endSpawn: Location?
        get() = Config.challengeDataConfig.getLocation(id, "endSpawn")
        set(value) = Config.challengeDataConfig.setLocation(id, "endSpawn", value!!)

    override val id: String = "level-border"
    override val material: Material = Material.EXPERIENCE_BOTTLE
    override val usesEvents: Boolean = true

    private fun initializeBorder(player: Player, location: Location, size: Double) {
        val packet = StckUtilsPlugin.protocolManager!!.createPacket(PacketType.Play.Server.INITIALIZE_BORDER)

        packet.doubles
            .write(0, location.blockX.toDouble())
            .write(1, location.blockZ.toDouble())
            .write(3, size)

        try {
            StckUtilsPlugin.protocolManager!!.sendServerPacket(player, packet)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot send packet $packet", e)
        }
    }

    private fun borderCenter(player: Player, location: Location) {
        val packet = StckUtilsPlugin.protocolManager!!.createPacket(PacketType.Play.Server.SET_BORDER_CENTER)

        if (location.world.environment == World.Environment.NETHER) {
            val loc = location.multiply(8.0)
            packet.doubles
                .write(0, loc.blockX.toDouble())
                .write(1, loc.blockZ.toDouble())
        } else {
            packet.doubles
                .write(0, location.blockX.toDouble())
                .write(1, location.blockZ.toDouble())
        }

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

    private fun getSpawn(environment: World.Environment) =
        when (environment) {
            World.Environment.NETHER -> {
                netherSpawn
            }
            World.Environment.THE_END -> {
                endSpawn
            }
            else -> {
                worldSpawn
            }
        }?.toBlockLocation()

    private fun setSpawn(location: Location) {
        when (location.world.environment) {
            World.Environment.NETHER -> {
                netherSpawn = location.toBlockLocation()
            }
            World.Environment.THE_END -> {
                endSpawn = location.toBlockLocation()
            }
            else -> {
                worldSpawn = location.toBlockLocation()
            }
        }
    }

    override fun onToggle() {
        if (!active) {
            onlinePlayers.forEach {
                resetBorder(it)
            }
        } else {
            onlinePlayers.forEach { player ->
                if (isFirstRun) {
                    player.level = 0
                    player.exp = 0F
                }
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
            if (getSpawn(World.Environment.NORMAL) == null) {
                KSpigotMainInstance.server.worlds.forEach {
                    if (it.environment == World.Environment.NORMAL) {
                        setSpawn(it.spawnLocation)
                        return@forEach
                    }
                }
            }
            onlinePlayers.forEach { player ->
                player.level = xpLevel
                player.exp = xpProgress.toFloat()
                if (!player.isInArea(getSpawn(player.world.environment)!!, ((xpLevel * 2) + 1.5))) {
                    player.teleportAsync(getSpawn(player.world.environment)!!)
                }
                task(
                    sync = false,
                    delay = 1L
                ) {
                    borderCenter(player, getSpawn(player.world.environment)!!)
                    task(
                        sync = false,
                        delay = 1L
                    ) {
                        border(player, ((xpLevel * 2) + 1).toDouble())
                    }
                }
            }
        }
    }

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = translatable(nameKey).coloredString(locale)
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, getGoBackItem(locale)) { it.player.openGUI(settingsGUI(locale), GUIPage.challengesPageNumber) }

            button(Slots.RowThreeSlotFive, resetItem()) {
                it.bukkitEvent.isCancelled = true
                isFirstRun = true
                xpLevel = 0
                xpProgress = 0.0
                worldSpawn = null
                netherSpawn = null
                endSpawn = null
                onToggle()
            }
        }
    }

    private fun resetItem() = itemStack(Material.BARRIER) {
        meta {
            name = translatable("$id.reset_item.name")
            addLore {
                addComponent(translatable("$id.reset_item.lore"))
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
        val tempXpLevel = xpLevel
        xpLevel = event.player.level
        onlinePlayers.forEach { player ->
            player.exp = xpProgress.toFloat()
            player.level = xpLevel
            borderCenter(player, getSpawn(player.world.environment)!!)
            moveBorder(player, ((tempXpLevel * 2) + 1).toDouble(), ((xpLevel * 2) + 1).toDouble(), 500L * abs((tempXpLevel * 2) - (xpLevel * 2)))
            if (!player.isInArea(getSpawn(player.world.environment)!!, ((xpLevel * 2) + 1.5))) {
                player.teleportAsync(getSpawn(player.world.environment)!!)
            }
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val tempXpLevel = xpLevel
        xpProgress = 0.0
        xpLevel = if (event.droppedExp <= 352) {
            (sqrt(1.0 * event.droppedExp + 9) - 3).toInt()
        } else if (event.droppedExp <= 1507) {
            (8.1 + sqrt(1.0 * 2 / 5 * (event.droppedExp - 7839 / 40))).toInt()
        } else {
            (325 / 18 + sqrt(1.0 * 2 / 9 * (event.droppedExp - 54215 / 72))).toInt()
        }
        event.newLevel = xpLevel
        event.newExp = 0
        event.droppedExp = 0
        onlinePlayers.forEach { player ->
            player.exp = xpProgress.toFloat()
            player.level = xpLevel
            borderCenter(player, getSpawn(player.world.environment)!!)
            moveBorder(player, ((tempXpLevel * 2) + 1).toDouble(), ((xpLevel * 2) + 1).toDouble(), 500L * abs((tempXpLevel * 2) - (xpLevel * 2)))
            if (!player.isInArea(getSpawn(player.world.environment)!!, ((xpLevel * 2) + 1.5))) {
                player.teleportAsync(getSpawn(player.world.environment)!!)
            }
        }
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        event.player.level = xpLevel
        event.player.exp = 0F
        event.player.teleportAsync(getSpawn(World.Environment.NORMAL)!!)
        task(
            sync = false,
            delay = 5L
        ) {
            borderCenter(event.player, getSpawn(event.player.world.environment)!!)
            task(
                sync = false,
                delay = 5L
            ) {
                border(event.player, ((xpLevel * 2) + 1).toDouble())
            }
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.level = xpLevel
        event.player.exp = xpProgress.toFloat()
        if (!event.player.isInArea(getSpawn(event.player.world.environment)!!, ((xpLevel * 2) + 1.5))) {
            event.player.teleportAsync(getSpawn(event.player.world.environment)!!)
        }
        task(
            sync = false,
            delay = 5L
        ) {
            borderCenter(event.player, getSpawn(event.player.world.environment)!!)
            task(
                sync = false,
                delay = 5L
            ) {
                border(event.player, ((xpLevel * 2) + 1).toDouble())
            }
        }
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        if (!event.player.isPlaying()) {
            return
        }
        if (getSpawn(event.to.world.environment) == null) {
            setSpawn(event.to)
        }
        if (!event.player.isInArea(getSpawn(event.to.world.environment)!!, ((xpLevel * 2) + 1.5))) {
            event.player.teleportAsync(getSpawn(event.to.world.environment)!!)
        }
        task(
            sync = false,
            delay = 5L
        ) {
            borderCenter(event.player, getSpawn(event.player.world.environment)!!)
            task(
                sync = false,
                delay = 5L
            ) {
                border(event.player, ((xpLevel * 2) + 1).toDouble())
            }
        }
    }
}
