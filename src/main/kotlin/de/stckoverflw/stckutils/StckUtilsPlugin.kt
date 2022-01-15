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
import de.stckoverflw.stckutils.extension.Colors
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
import de.stckoverflw.stckutils.util.getSettingsItem
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigot
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Bukkit
import org.bukkit.StructureType
import java.nio.file.Files
import kotlin.io.path.div

class StckUtilsPlugin : KSpigot() {

    companion object {
        const val prefix: String = "StckUtils | "
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
        if (server.pluginManager.getPlugin("ProtocolLib") != null) {
            protocolManager = ProtocolLibrary.getProtocolManager()
            isProtocolLib = true
        }

        Config.reloadPositions()

        if (Config.resetSettingsConfig.villageSpawn && wasReset) {
            val world = Bukkit.getWorld("world")!!
            val nearestVillage = world.locateNearestStructure(world.spawnLocation, StructureType.VILLAGE, 10000, true)
            world.spawnLocation = nearestVillage!!
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
            translatable(
                "console.enabled.enabled",
                listOf(
                    text(pluginDescription.name)
                        .color(Colors.PRIMARY),
                    text(pluginDescription.version)
                        .color(Colors.PRIMARY)
                )
            )
                .color(KColors.YELLOW.asTextColor())
                .coloredString()
        )
        logger.info(
            translatable(
                "console.enabled.authors",
                listOf(
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
                        .color(Colors.PRIMARY)
                )
            )
                .color(KColors.YELLOW.asTextColor())
                .coloredString()
        )
        if (pluginDescription.apiVersion != null) {
            logger.info(
                translatable(
                    "console.enabled.api_version",
                    listOf(
                        text(pluginDescription.apiVersion ?: "n/a")
                            .color(Colors.PRIMARY)
                    )
                )
                    .color(KColors.YELLOW.asTextColor())
                    .coloredString()
            )
        }
        if (pluginDescription.website != null) {
            logger.info(
                translatable(
                    "console.enabled.website",
                    listOf(
                        text(pluginDescription.website ?: "n/a")
                            .color(Colors.PRIMARY)
                    )
                )
                    .color(KColors.YELLOW.asTextColor())
                    .coloredString()
            )
        }

        onlinePlayers.forEach {
            it.inventory.clear()
            if (it.isOp) {
                it.inventory.setItem(8, getSettingsItem(it.locale()))
            }
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
                    "error.delete_worlds",
                    listOf(
                        text(world)
                            .color(Colors.ERROR_ARGS)
                    )
                )
                    .color(Colors.ERROR)
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
