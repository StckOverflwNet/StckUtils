package de.stckoverflw.stckutils

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import de.stckoverflw.stckutils.command.AllXCommand
import de.stckoverflw.stckutils.command.DefaultLanguageCommand
import de.stckoverflw.stckutils.command.HideCommand
import de.stckoverflw.stckutils.command.PositionCommand
import de.stckoverflw.stckutils.command.SettingsCommand
import de.stckoverflw.stckutils.command.TimerCommand
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.asTextColor
import de.stckoverflw.stckutils.extension.coloredString
import de.stckoverflw.stckutils.extension.saveInventory
import de.stckoverflw.stckutils.i18n.TranslationsProvider
import de.stckoverflw.stckutils.listener.ConnectionListener
import de.stckoverflw.stckutils.listener.InteractListener
import de.stckoverflw.stckutils.listener.ProtectionListener
import de.stckoverflw.stckutils.listener.RespawnListener
import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.util.Colors
import de.stckoverflw.stckutils.util.Permissions
import de.stckoverflw.stckutils.util.getSettingsItem
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigot
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.space
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Bukkit
import org.bukkit.StructureType
import org.bukkit.World
import java.nio.file.Files
import kotlin.io.path.div

class StckUtilsPlugin : KSpigot() {

    companion object {
        val prefix: Component =
            literalText {
                component(
                    literalText {
                        text("StckUtils")
                        color = KColors.WHITE.asTextColor()
                        bold = true
                    }
                )
                component(space())
                component(
                    literalText {
                        text("|")
                        color = KColors.GRAY.asTextColor()
                    }
                )
                component(space())
            }
        var protocolManager: ProtocolManager? = null
        var isProtocolLib: Boolean = false
        val translationsProvider = TranslationsProvider()
    }

    private var wasReset = false

    override fun load() {
        Config()
        if (Config.resetSettingsConfig.shouldReset) {
            deleteWorld("world")
            deleteWorld("world_nether")
            deleteWorld("world_the_end")
            saveResource("Data/Positions/positions.yml", true)
            saveResource("Data/Timer/timer.yml", true)
            wasReset = true
            Config.resetSettingsConfig.shouldReset = false
        }
    }

    override fun startup() {
        if (onlinePlayers.isNotEmpty()) {
            logger.severe("It looks like you've reloaded, please restart instead!")
        }

        if (server.pluginManager.getPlugin("ProtocolLib") != null) {
            protocolManager = ProtocolLibrary.getProtocolManager()
            isProtocolLib = true
        }

        Config.reloadPositions()

        if (Config.resetSettingsConfig.villageSpawn && wasReset) {
            val world = server.worlds.first { it.environment == World.Environment.NORMAL }
            val nearestVillage = world.locateNearestStructure(world.spawnLocation, StructureType.VILLAGE, 10000, false)
            world.spawnLocation = nearestVillage ?: world.spawnLocation
            wasReset = false
        }

        Timer()
        ChallengeManager()
        GameChangeManager()
        GoalManager()
        translationsProvider.registerTranslations()

        pluginManager.registerEvents(ConnectionListener(), this)
        pluginManager.registerEvents(InteractListener(), this)
        pluginManager.registerEvents(ProtectionListener(), this)
        pluginManager.registerEvents(RespawnListener(), this)

        TimerCommand().register()
        SettingsCommand().register()
        HideCommand().register()
        PositionCommand().register()
        AllXCommand().register()
        DefaultLanguageCommand().register()

        val pluginDescription = this.description

        logger.info(
            translatable("console.enabled.enabled")
                .color(Colors.SECONDARY)
                .args(
                    text(pluginDescription.name)
                        .color(Colors.PRIMARY),
                    text(pluginDescription.version)
                        .color(Colors.PRIMARY)
                )
                .coloredString()
        )
        logger.info(
            translatable(
                "console.enabled.authors"
            )
                .color(Colors.SECONDARY)
                .args(
                    text(
                        if (pluginDescription.authors.size <= 1) {
                            pluginDescription.authors.joinToString("")
                        } else {
                            val authors = pluginDescription.authors.sorted().toMutableList()
                            authors.add(
                                authors.size - 1,
                                translatable("generic.and").coloredString()
                            )
                            authors.joinToString(" ")
                        }
                    )
                )
                .color(Colors.PRIMARY)
                .coloredString()
        )
        if (pluginDescription.apiVersion != null) {
            logger.info(
                translatable(
                    "console.enabled.api_version"
                )
                    .color(Colors.SECONDARY)
                    .args(
                        text(pluginDescription.apiVersion ?: "n/a")
                            .color(Colors.PRIMARY)
                    )
                    .coloredString()
            )
        }
        if (pluginDescription.website != null) {
            logger.info(
                translatable(
                    "console.enabled.website"
                )
                    .color(Colors.SECONDARY)
                    .args(
                        text(pluginDescription.website ?: "n/a")
                            .color(Colors.PRIMARY)
                    )
                    .coloredString()
            )
        }

        onlinePlayers.filter { it.hasPermission(Permissions.SETTINGS_ITEM) && it.inventory.isEmpty }
            .forEach {
                it.inventory.setItem(8, getSettingsItem(it.locale()))
            }
    }

    override fun shutdown() {
        if (Timer.running) {
            onlinePlayers.forEach {
                it.saveInventory()
                it.inventory.clear()
            }
        }
    }

    private fun deleteWorld(world: String) {
        val worldPath = Bukkit.getWorldContainer().toPath() / world
        try {
            Files.walk(worldPath).sorted(Comparator.reverseOrder()).forEach {
                Files.delete(it)
            }
        } catch (e: Exception) {
            logger.warning(
                translatable(
                    "error.delete_worlds"
                )
                    .color(Colors.ERROR)
                    .args(
                        text(world)
                            .color(Colors.ERROR_ARGS)
                    )
                    .coloredString()
            )
            logger.warning(e.stackTraceToString())
        }
        Files.createDirectories(worldPath)
        Files.createDirectories(worldPath / "data")
        Files.createDirectories(worldPath / "datapacks")
        Files.createDirectories(worldPath / "playerdata")
        Files.createDirectories(worldPath / "poi")
        Files.createDirectories(worldPath / "region")
    }
}
