package de.stckoverflw.stckutils.minecraft.timer

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.saveInventory
import de.stckoverflw.stckutils.extension.setSavedInventory
import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.gamechange.active
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.util.Permissions
import de.stckoverflw.stckutils.util.settingsItem
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.sync
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.entity.Creature
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

object Timer {

    private var initialized = false

    var direction: TimerDirection
        get() = TimerDirection.valueOf((Config.timerConfig.getSetting("direction") ?: TimerDirection.FORWARDS.name) as String)
        set(value) {
            Config.timerConfig.setSetting("direction", value.name)
            if (value == TimerDirection.BACKWARDS) {
                backwardsStartTime = time
            }
        }
    var joinWhileRunning: List<AccessLevel>
        get() = (Config.timerConfig.getSettingList("joinWhileRunning") ?: listOf(AccessLevel.OPERATOR.name)).map { AccessLevel.valueOf(it as String) }
        set(value) = Config.timerConfig.setSetting("joinWhileRunning", value.map { it.name })
    var color: String
        get() {
            var col = Config.timerConfig.getSetting("color")
            if (col == null) {
                Config.timerConfig.setSetting("color", "§c")
                col = "§c"
            }
            return col as String
        }
        set(value) = Config.timerConfig.setSetting("color", value)
    var time: Long
        get() = (Config.timerDataConfig.getSetting("time") ?: 0).toString().toLong()
        set(value) {
            Config.timerDataConfig.setSetting("time", value)
            if (direction == TimerDirection.BACKWARDS && backwardsStartTime == 0L) {
                backwardsStartTime = time
            }
        }
    var backwardsStartTime: Long
        get() = (Config.timerDataConfig.getSetting("startTime") ?: 0).toString().toLong()
        set(value) = Config.timerDataConfig.setSetting("startTime", value)
    var running = false
    var additionalInfo: ArrayList<String> = arrayListOf()

    operator fun invoke() {
        require(!initialized) { "Timer has been initialized already" }
        initialized = true

        task(
            sync = false,
            delay = 0,
            period = 20
        ) {
            if (running) {
                when (direction) {
                    TimerDirection.FORWARDS -> {
                        time++
                    }
                    TimerDirection.BACKWARDS -> {
                        if (time == 0L) {
                            sync {
                                ChallengeManager.challenges.first().lose("Time has run out.")
                            }
                        } else {
                            time--
                        }
                    }
                }
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
            it.sendActionBar(
                text(
                    this.formatTime() + if (additionalInfo.isNotEmpty()) " (${
                    additionalInfo.joinToString(
                        " "
                    )
                    })" else ""
                )
            )
        }
    }

    private fun broadcastIdle() {
        Bukkit.getOnlinePlayers().forEach {
            it.sendActionBar(text("$color§lTimer paused"))
        }
    }

    fun start(): Boolean {
        return if (running) {
            false
        } else {
            backwardsStartTime = if (direction == TimerDirection.BACKWARDS && backwardsStartTime == 0L) {
                time
            } else {
                0
            }
            onlinePlayers.forEach {
                it.inventory.clear()
                it.setSavedInventory()
            }
            GameChangeManager.registerGameChangeListeners()
            ChallengeManager.registerChallengeListeners()
            running = true
            GameChangeManager.gameChanges.filter { it.active }.forEach { change ->
                change.run()
                change.onTimerToggle()
            }
            ChallengeManager.challenges.filter { it.active }.forEach { challenge ->
                challenge.prepareChallenge()
                challenge.onTimerToggle()
            }
            GoalManager.registerActiveGoal()
            GoalManager.activeGoal?.onTimerToggle()
            true
        }
    }

    fun stop(): Boolean {
        return if (!running) {
            false
        } else {
            onlinePlayers.forEach { player ->
                player.saveInventory()
                player.inventory.clear()
                player.getNearbyEntities(48.0, 48.0, 48.0).forEach {
                    if (it is Creature)
                        it.target = null
                }
                if (player.hasPermission(Permissions.SETTINGS_ITEM)) {
                    player.inventory.setItem(8, settingsItem)
                }
            }
            ChallengeManager.unregisterChallengeListeners()
            GameChangeManager.unregisterGameChangeListeners()
            GoalManager.unregisterActiveGoal()
            running = false
            ChallengeManager.challenges.filter { it.active }.forEach { challenge ->
                challenge.onTimerToggle()
            }
            GameChangeManager.gameChanges.filter { it.active }.forEach { change ->
                change.onTimerToggle()
            }
            GoalManager.activeGoal?.onTimerToggle()
            true
        }
    }

    fun reset() {
        time = 0
    }

    override fun toString() = if (direction == TimerDirection.FORWARDS) {
        formatTime()
    } else {
        formatTime(backwardsStartTime)
    }

    @OptIn(ExperimentalTime::class)
    fun formatTime(
        seconds: Long = time
    ): String {
        val duration = seconds.seconds
        duration.toComponents(
            action = { days, hours, min, sec, _ ->
                return (
                    "$color§l" + (if (days != 0L) "${days}d " else "") +
                        (if (hours != 0) "${hours}h " else "") +
                        (if (min != 0) "${min}m " else "") +
                        if (sec != 0) "$sec" + if (days + hours + min == 0L) " second" + if (sec != 1) "s" else "" else "s" else ""
                    )
            }
        )
    }
}
