package de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule

import com.destroystokyo.paper.MaterialTags
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.asTextColor
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.minecraft.gamechange.GameRule
import de.stckoverflw.stckutils.minecraft.gamechange.active
import de.stckoverflw.stckutils.minecraft.gamechange.descriptionKey
import de.stckoverflw.stckutils.minecraft.gamechange.nameKey
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.Colors
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.server
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.flags
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.Locale
import java.util.UUID

object SpawnWorld : GameRule() {

    private var environment: World.Environment
        get() = World.Environment.valueOf(Config.gameChangeConfig.getSetting(id, "environment") as String? ?: "NORMAL")
        set(value) = Config.gameChangeConfig.setSetting(id, "environment", value.name)

    private var players: List<UUID>
        get() = (Config.gameChangeDataConfig.getSettingList(id, "players") ?: listOf()).map { UUID.fromString(it as String) }
        set(value) = Config.gameChangeDataConfig.setSetting(id, "players", value.map { it.toString() })

    override val id: String = "spawn-world"
    override val usesEvents: Boolean = true

    override fun item(locale: Locale): ItemStack = itemStack(
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
            name = translatable(nameKey)
                .color(Colors.GAME_CHANGE_COMPOUND)
                .render(locale)
            addLore {
                addComponent(
                    translatable(descriptionKey)
                        .args(
                            translatable(
                                when (environment) {
                                    World.Environment.NORMAL -> "$id.overworld"
                                    World.Environment.NETHER -> "$id.nether"
                                    else -> "$id.end"
                                }
                            )
                                .color(KColors.DARKGRAY.asTextColor())
                        )
                        .color(Colors.GAME_CHANGE_COMPOUND_SECONDARY)
                        .render(locale)
                )
            }
            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
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
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item(event.player.locale()))
        run()
    }

    private fun getSpawnLocation(): Location {
        val locationWorld = server.worlds.first { it.environment == environment }
        return when (environment) {
            World.Environment.NORMAL,
            World.Environment.NETHER,
            -> locationWorld.spawnLocation
            else ->
                locationWorld
                    .locateNearestBiome(locationWorld.spawnLocation.add(1000.0, 0.0, 1000.0), Biome.END_HIGHLANDS, 500)
                    ?: locationWorld.spawnLocation
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
        onlinePlayers.filter { it.isPlaying() && players.contains(it.uniqueId) }.forEach {
            if (it.inventory.none { item -> MaterialTags.PICKAXES.values.contains(item?.type) }) {
                val pickaxe = itemStack(Material.IRON_PICKAXE) {
                    meta {
                        name = translatable("$id.starter_pickaxe.name")
                    }
                }
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
                    name = translatable("$id.starter_pickaxe.name")
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
