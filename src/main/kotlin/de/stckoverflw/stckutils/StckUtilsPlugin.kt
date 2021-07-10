package de.stckoverflw.stckutils

import de.stckoverflw.stckutils.challenge.ChallengeManager
import de.stckoverflw.stckutils.commands.SettingsCommand
import de.stckoverflw.stckutils.commands.TimerCommand
import de.stckoverflw.stckutils.gamechange.GameChangeManager
import de.stckoverflw.stckutils.goal.GoalManager
import de.stckoverflw.stckutils.listener.ConnectionListener
import de.stckoverflw.stckutils.listener.InteractListener
import de.stckoverflw.stckutils.listener.ProtectionListener
import de.stckoverflw.stckutils.timer.Timer
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigot

class StckUtilsPlugin : KSpigot() {

    companion object {
        const val prefix: String = "§f§lStckUtils §7| §r"
    }

    override fun startup() {
        Timer(0)
        ChallengeManager()
        GoalManager()
        GameChangeManager()

        pluginManager.registerEvents(ConnectionListener(), this)
        pluginManager.registerEvents(InteractListener(), this)
        pluginManager.registerEvents(ProtectionListener(), this)

        getCommand("timer")!!.setExecutor(TimerCommand())
        getCommand("timer")!!.tabCompleter = TimerCommand()
        getCommand("settings")!!.setExecutor(SettingsCommand())

        val pluginDescription = this.description
        logger.info("§aEnabled §3${pluginDescription.name} §aversion §3${pluginDescription.version}")
        logger.info("§aThis Plugin was made by §3${pluginDescription.authors.joinToString(", ")}")
        logger.info("§aUsing API-Version §3${pluginDescription.apiVersion!!}")
        logger.info("§aMore Information at §3${pluginDescription.website}")
    }
}