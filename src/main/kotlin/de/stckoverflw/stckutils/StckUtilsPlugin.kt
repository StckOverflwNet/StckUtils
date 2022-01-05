package de.stckoverflw.stckutils

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import de.stckoverflw.stckutils.command.*
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.language
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
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigot
import org.bukkit.Bukkit
import org.bukkit.StructureType
import java.nio.file.Files
import java.util.*
import kotlin.io.path.div

class StckUtilsPlugin : KSpigot() {

    companion object {
        const val prefix: String = "§f§lStckUtils §7| §r"
        var protocolManager: ProtocolManager? = null
        var isProtocolLib: Boolean = false
        val translationsProvider: TranslationsProvider = TranslationsProvider()
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
        GoalManager()
        GoalManager.registerActiveGoal()
        GoalManager.unregisterActiveGoal()
        GameChangeManager()

        pluginManager.registerEvents(ConnectionListener(), this)
        pluginManager.registerEvents(InteractListener(), this)
        pluginManager.registerEvents(ProtectionListener(), this)
        pluginManager.registerEvents(RespawnListener(), this)

        TimerCommand().register()
        SettingsCommand().register()
        HideCommand().register()
        PositionCommand().register()
        AllXCommand().register()
        LanguageCommand().register()

        val pluginDescription = this.description

        Config.languageConfig.defaultLanguage = Locale.GERMAN

        logger.info(
            translationsProvider.translate(
                "console.enabled.enabled",
                Config.languageConfig.defaultLanguage,
                "messages",
                arrayOf(pluginDescription.name, pluginDescription.version)
            )
        )
        logger.info(
            translationsProvider.translate(
                "console.enabled.authors",
                Config.languageConfig.defaultLanguage,
                "messages",
                arrayOf(
                    if (pluginDescription.authors.size <= 1) {
                        pluginDescription.authors
                    } else {
                        val authors = pluginDescription.authors.sorted().toMutableList()
                        authors.add(
                            authors.size - 1,
                            translationsProvider.translate(
                                "generic.and",
                                Config.languageConfig.defaultLanguage,
                                "general"
                            )
                        )
                        authors.joinToString(" ")
                    }
                )
            )
        )
        if (pluginDescription.apiVersion != null) {
            logger.info(
                translationsProvider.translate(
                    "console.enabled.api_version",
                    Config.languageConfig.defaultLanguage,
                    "messages",
                    arrayOf(pluginDescription.apiVersion)
                )
            )
        }
        if (pluginDescription.website != null) {
            logger.info(
                translationsProvider.translate(
                    "console.enabled.website",
                    Config.languageConfig.defaultLanguage,
                    "messages",
                    arrayOf(pluginDescription.website)
                )
            )
        }

        onlinePlayers.forEach {
            it.inventory.clear()
            if (it.isOp) {
                it.inventory.setItem(8, getSettingsItem(it.language))
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
                translationsProvider.translate(
                    "error.delete_worlds",
                    Config.languageConfig.defaultLanguage,
                    "messages",
                    arrayOf(world)
                )
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
