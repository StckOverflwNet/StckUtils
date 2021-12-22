package de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule

import com.destroystokyo.paper.MaterialTags
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.gamechange.GameRule
import de.stckoverflw.stckutils.minecraft.gamechange.active
import de.stckoverflw.stckutils.minecraft.timer.Timer
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import java.util.*

object SpawnWorld : GameRule() {

    private var environment: World.Environment
        get() = World.Environment.valueOf(Config.gameChangeConfig.getSetting(id, "environment") as String? ?: "NORMAL")
        set(value) = Config.gameChangeConfig.setSetting(id, "environment", value.name)

    private var players: List<UUID>
        get() = (Config.gameChangeDataConfig.getSettingList(id, "players") ?: listOf()).map { UUID.fromString(it as String) }
        set(value) = Config.gameChangeDataConfig.setSetting(id, "players", value.map { it.toString() })

    override val id: String = "spawn-world"
    override val usesEvents: Boolean = true

    override fun item(): ItemStack = itemStack(
        when (environment) {
            World.Environment.NORMAL -> {
                Material.GRASS_BLOCK
            }
            World.Environment.NETHER -> {
                Material.NETHERRACK
            }
            else -> {
                Material.END_STONE
            }
        }
    ) {
        meta {
            name = "§aSpawn World"
            addLore {
                +" "
                +"§7Sets the world to spawn in"
                +" "
                when (environment) {
                    World.Environment.NORMAL -> {
                        +"§aOverworld"
                    }
                    World.Environment.NETHER -> {
                        +"§4Nether"
                    }
                    else -> {
                        +"§eEnd (End Islands, not Ender Dragon Island)"
                    }
                }
            }
        }
    }

    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        active = true
        environment = when (environment) {
            World.Environment.NORMAL -> {
                World.Environment.NETHER
            }
            World.Environment.NETHER -> {
                World.Environment.THE_END
            }
            else -> {
                World.Environment.NORMAL
            }
        }
        players = listOf()
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item())
        run()
    }

    private fun getSpawnLocation(): Location {
        val locationWorld = Bukkit.getWorlds().first { it.environment == environment }
        return when (environment) {
            World.Environment.NORMAL,
            World.Environment.NETHER -> {
                locationWorld.spawnLocation
            }
            else -> {
                locationWorld.locateNearestBiome(locationWorld.spawnLocation.add(1000.0, 0.0, 1000.0), Biome.END_HIGHLANDS, 500)
                    ?: locationWorld.spawnLocation
            }
        }
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        if (!event.player.isPlaying() ||
            event.isAnchorSpawn ||
            event.isBedSpawn
        ) {
            return
        }
        event.respawnLocation = getSpawnLocation()
    }

    override fun onTimerToggle() {
        if (!Timer.running ||
            environment != World.Environment.THE_END
        ) {
            return
        }
        val pickaxe = itemStack(Material.IRON_PICKAXE) {
            meta {
                name = "§aEnd Starter Pickaxe"
            }
        }
        onlinePlayers.filter { it.isPlaying() && players.contains(it.uniqueId) }.forEach {
            if (it.inventory.none { item -> MaterialTags.PICKAXES.values.contains(item?.type) }) {
                it.inventory.addItem(pickaxe)
            }
        }
    }

    override fun run() {
        val location = getSpawnLocation()
        onlinePlayers.filter { it.isPlaying() && !players.contains(it.uniqueId) }.forEach {
            it.teleportAsync(location)
            val pickaxe = itemStack(Material.IRON_PICKAXE) {
                meta {
                    name = "§aEnd Starter Pickaxe"
                }
            }
            if (environment == World.Environment.THE_END &&
                Timer.running
            ) {
                it.inventory.addItem(pickaxe)
            }
            players = players.plus(it.uniqueId)
        }
    }
}
