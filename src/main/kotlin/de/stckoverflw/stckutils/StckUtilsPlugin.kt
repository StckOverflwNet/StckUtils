package de.stckoverflw.stckutils

import de.stckoverflw.stckutils.challenge.ChallengeManager
import de.stckoverflw.stckutils.commands.SettingsCommand
import de.stckoverflw.stckutils.commands.TimerCommand
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.gamechange.GameChangeManager
import de.stckoverflw.stckutils.goal.GoalManager
import de.stckoverflw.stckutils.listener.ConnectionListener
import de.stckoverflw.stckutils.listener.InteractListener
import de.stckoverflw.stckutils.listener.ProtectionListener
import de.stckoverflw.stckutils.timer.Timer
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigot
import org.bukkit.Bukkit
import org.bukkit.StructureType
import org.bukkit.World
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class StckUtilsPlugin : KSpigot() {

    companion object {
        const val prefix: String = "§f§lStckUtils §7| §r"
    }

    var wasReset = false

    override fun load() {
        Config()
        if (Config.resetSettings.shouldReset) {
            deleteWorld("world")
            deleteWorld("world_nether")
            deleteWorld("world_the_end")
            wasReset = true
            Config.resetSettings.shouldReset = false
        }
    }

    override fun startup() {
        if (Config.resetSettings.villageSpawn && wasReset) {
            val world = Bukkit.getWorld("world")!!
            val nearestVillage = world.locateNearestStructure(world.spawnLocation, StructureType.VILLAGE, 10000, true)
            world.spawnLocation = nearestVillage!!
            wasReset = false
        }

        Timer(0)
        ChallengeManager()
        GoalManager()
        GameChangeManager()

        pluginManager.registerEvents(ConnectionListener(), this)
        pluginManager.registerEvents(InteractListener(), this)
        pluginManager.registerEvents(ProtectionListener(), this)

        val timerCommand = TimerCommand()
        getCommand("timer")!!.setExecutor(timerCommand)
        getCommand("timer")!!.tabCompleter = timerCommand
        getCommand("settings")!!.setExecutor(SettingsCommand())

        val pluginDescription = this.description
        logger.info("§aEnabled §3${pluginDescription.name} §aversion §3${pluginDescription.version}")
        logger.info("§aThis Plugin was made by §3${pluginDescription.authors.joinToString(", ")}")
        logger.info("§aUsing API-Version §3${pluginDescription.apiVersion!!}")
        logger.info("§aMore Information at §3${pluginDescription.website}")
    }

    private fun deleteWorld(world: String) {
        val worldFile = File(Bukkit.getWorldContainer(), world)
        try {
            Files.walk(worldFile.toPath()).sorted(Comparator.reverseOrder()).map { obj: Path -> obj.toFile() }
                .forEach { obj: File -> obj.delete() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        worldFile.mkdirs()
        File(worldFile, "data").mkdirs()
        File(worldFile, "datapacks").mkdirs()
        File(worldFile, "playerdata").mkdirs()
        File(worldFile, "poi").mkdirs()
        File(worldFile, "region").mkdirs()
    }
}