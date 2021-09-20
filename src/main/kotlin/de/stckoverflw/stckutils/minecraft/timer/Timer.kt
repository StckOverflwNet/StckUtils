package de.stckoverflw.stckutils.minecraft.timer

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.saveInventory
import de.stckoverflw.stckutils.extension.setSavedInventory
import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.util.settingsItem
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Creature
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

object Timer {

    private var initialized = false

    private var color: String
        get() {
            var col = Config.timerConfig.getSetting("color")
            if (col == null) {
                Config.timerConfig.setSetting("color", "§c")
                col = "§c"
            }
            return col as String
        }
        set(value) = Config.timerConfig.setSetting("color", value)

    var time: Long = 0
    var running = false

    operator fun invoke(startTime: Long) {
        require(!initialized) { "Timer has been initialized already" }
        initialized = true

        time = startTime

        task(
            sync = false,
            delay = 0,
            period = 20
        ) {
            if (running) {
                time++
                ChallengeManager.challenges.forEach { challenge ->
                    if (challenge.active) {
                        challenge.update()
                    }
                }
                broadcastTimer()
            } else {
                broadcastIdle()
            }
        }
    }

    private fun broadcastTimer() {
        Bukkit.getOnlinePlayers().forEach {
            it.sendActionBar(Component.text(this.formatTime()))
        }
    }

    private fun broadcastIdle() {
        Bukkit.getOnlinePlayers().forEach {
            it.sendActionBar(Component.text("$color§lTimer paused"))
        }
    }

    fun start(): Boolean {
        return if (running) {
            false
        } else {
            GameChangeManager.registerGameChangeListeners()
            ChallengeManager.registerChallengeListeners()
            GameChangeManager.gameChanges.forEach { change ->
                change.run()
            }
            ChallengeManager.registerChallengeListeners()
            ChallengeManager.challenges.forEach { challenge ->
                if (challenge.active) {
                    challenge.prepareChallenge()
                }
            }
            GoalManager.registerActiveGoal()
            onlinePlayers.forEach {
                it.inventory.clear()
                it.setSavedInventory()
            }
            running = !running
            true
        }
    }

    fun stop(): Boolean {
        return if (!running) {
            false
        } else {
            ChallengeManager.unregisterChallengeListeners()
            GameChangeManager.unregisterGameChangeListeners()
            running = !running
            onlinePlayers.forEach { player ->
                player.saveInventory()
                player.inventory.clear()
                player.getNearbyEntities(48.0, 48.0, 48.0).forEach {
                    if (it is Creature)
                        it.target = null
                }
                if (player.isOp) {
                    player.inventory.setItem(8, settingsItem)
                }
            }
            true
        }
    }

    fun reset() {
        time = 0
    }

    override fun toString() = formatTime()

    @OptIn(ExperimentalTime::class)
    fun formatTime(seconds: Long = time): String {
        val duration = Duration.seconds(seconds)
        duration.toComponents(
            action = { days, hours, min, sec, _ ->
                return (
                    "$color§l" + (if (days != 0) "${days}d " else "") +
                        (if (hours != 0) "${hours}h " else "") +
                        (if (min != 0) "${min}m " else "") +
                        if (sec != 0) "$sec" + if (days + hours + min == 0) " second" + if (sec != 1) "s" else "" else "s" else ""
                    )
            }
        )
    }
}
