package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.minecraft.timer.Timer
import de.stckoverflw.stckutils.user.settingsGUI
import net.axay.kspigot.gui.openGUI
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TimerCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender.isOp) {
            if (args.isNotEmpty()) {
                when (args[0].lowercase()) {
                    "resume" -> {
                        Timer.start()
                        Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §astarted"))
                    }
                    "pause" -> {
                        Timer.stop()
                        Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §6stopped"))
                    }
                    "reset" -> {
                        Timer.reset()
                        Bukkit.broadcast(Component.text(StckUtilsPlugin.prefix + "§7The Timer was §cresetted"))
                    }
                }
            } else {
                if (sender is Player) {
                    sender.openGUI(settingsGUI(), 5)
                } else {
                    sender.sendMessage(StckUtilsPlugin.prefix + "§cYou can't open a GUI since you are not a Player")
                }
            }
        } else {
            sender.sendMessage(StckUtilsPlugin.prefix + "§cYou don't have Permission to use that Command!")
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        var completions = listOf("")
        if (args.isEmpty()) {
            return null
        } else if (args.size == 1) {
            completions = listOf("resume", "pause", "reset")
        }
        return completions.filter { it.startsWith(args[0], true) }.sorted()
    }
}
