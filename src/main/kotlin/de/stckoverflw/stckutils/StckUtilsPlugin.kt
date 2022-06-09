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
import de.stckoverflw.stckutils.extension.saveInventory
import de.stckoverflw.stckutils.extension.split
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
import net.axay.kspigot.extensions.bukkit.plainText
import net.axay.kspigot.extensions.bukkit.render
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigot
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.space
import net.kyori.adventure.text.Component.translatable
import org.bukkit.StructureType
import org.bukkit.World
import org.bukkit.plugin.PluginDescriptionFile
import java.nio.file.Files
import java.util.Locale
import java.util.regex.Pattern
import kotlin.io.path.div

class StckUtilsPlugin : KSpigot() {

    companion object {
        val prefix: Component =
            literalText {
                component(
                    literalText {
                        text("StckUtils")
                        color = KColors.WHITE
                        bold = true
                    }
                )
                component(space())
                component(
                    literalText {
                        text("|")
                        color = KColors.GRAY
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
            logger.severe(
                translatable("do_not_reload_idiot")
                    .plainText()
            )
        }

        if (server.pluginManager.getPlugin("ProtocolLib") != null) {
            protocolManager = ProtocolLibrary.getProtocolManager()
            isProtocolLib = true
        }

        Config.reloadPositions()

        if (Config.resetSettingsConfig.villageSpawn && wasReset) {
            val world = server.worlds.first { it.environment == World.Environment.NORMAL }
            val nearestVillage = world.locateNearestStructure(world.spawnLocation, StructureType.VILLAGE, 10000, false)
            if (nearestVillage != null) {
                world.spawnLocation = nearestVillage
            } else {
                logger.info(
                    literalText {
                        component(translatable("no_village_found"))
                        color = Colors.ERROR
                    }
                        .plainText()
                )
            }
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

        sendPluginInfo(this.description)

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
        val worldPath = server.worldContainer.toPath() / world
        try {
            Files.walk(worldPath).sorted(Comparator.reverseOrder()).forEach {
                Files.delete(it)
            }
        } catch (e: Exception) {
            logger.warning(
                literalText {
                    component(
                        translatable("error.delete_worlds")
                            .args(
                                literalText {
                                    text(world)
                                    color = Colors.ERROR_SECONDARY
                                }
                            )
                            .render(Locale.US)
                    )
                    color = Colors.ERROR
                }
                    .plainText()
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

    private fun sendPluginInfo(pluginDescription: PluginDescriptionFile) {
        literalText {
            color = Colors.PRIMARY
            component(
                translatable("console.enabled.enabled")
                    .args(
                        literalText(pluginDescription.name) {
                            color = Colors.SECONDARY
                        },
                        literalText(pluginDescription.version) {
                            color = Colors.SECONDARY
                        }
                    )
                    .render(Locale.US)
            )
            newLine()
            component(
                translatable(
                    "console.enabled.authors"
                )
                    .args(
                        literalText {
                            text(
                                if (pluginDescription.authors.size <= 1) {
                                    pluginDescription.authors
                                } else {
                                    val authors = pluginDescription.authors.sorted().toMutableList()
                                    authors.add(
                                        authors.size - 1,
                                        translatable("generic.and").render(Locale.US).plainText()
                                    )
                                    authors
                                }
                                    .joinToString(" ")
                            )
                            color = Colors.PRIMARY
                        }
                    )
                    .render(Locale.US)
            )
            newLine()
            if (pluginDescription.apiVersion != null) {
                component(
                    translatable(
                        "console.enabled.api_version"
                    )
                        .args(
                            literalText(pluginDescription.apiVersion ?: "n/a") {
                                color = Colors.SECONDARY
                            }
                        )
                        .render(Locale.US)
                )
                newLine()
            }
            if (pluginDescription.website != null) {
                component(
                    translatable(
                        "console.enabled.website"
                    )
                        .args(
                            literalText(pluginDescription.website ?: "n/a") {
                                color = Colors.SECONDARY
                            }
                        )
                        .render(Locale.US)
                )
            }
        }.split(Pattern.compile("\\n")).forEach {
            logger.info(it.plainText())
        }
    }
}
