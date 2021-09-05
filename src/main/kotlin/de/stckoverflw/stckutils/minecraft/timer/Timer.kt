package de.stckoverflw.stckutils.minecraft.timer

import de.stckoverflw.stckutils.extension.saveInventory
import de.stckoverflw.stckutils.extension.setSavedInventory
import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.user.settingsItem
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Creature
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

object Timer {

    private var initialized = false

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
            it.sendActionBar(Component.text(this.toString()))
        }
    }

    private fun broadcastIdle() {
        Bukkit.getOnlinePlayers().forEach {
            it.sendActionBar(Component.text("§c§lTimer paused"))
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
            Bukkit.getOnlinePlayers().forEach {
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
            Bukkit.getOnlinePlayers().forEach { player ->
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

    @OptIn(ExperimentalTime::class)
    override fun toString(): String {
        val duration = Duration.seconds(time)
        duration.toComponents(action = { days, hours, minutes, seconds, _ ->
            return ("§c§l" + (if (days != 0) "${days}d " else "") +
                    (if (hours != 0) "${hours}h " else "") +
                    (if (minutes != 0) "${minutes}m " else "") +
                    if (seconds != 0) "${seconds}s" + if (days + hours + minutes == 0) "econd" + if (seconds != 1) "s" else "" else "" else "")
        })
    }
}
