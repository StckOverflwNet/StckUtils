package de.stckoverflw.stckutils.minecraft.timer

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.fromKey
import de.stckoverflw.stckutils.extension.plainText
import de.stckoverflw.stckutils.extension.saveInventory
import de.stckoverflw.stckutils.extension.sendPrefixMessage
import de.stckoverflw.stckutils.extension.setSavedInventory
import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.gamechange.active
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.util.Permissions
import de.stckoverflw.stckutils.util.getSettingsItem
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.sync
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.space
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Creature
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

object Timer {

    private var initialized = false

    var direction: TimerDirection
        get() = fromKey(Config.timerConfig.getSetting("direction") as? String ?: TimerDirection.FORWARDS.key) ?: TimerDirection.FORWARDS
        set(value) {
            Config.timerConfig.setSetting("direction", value.key)
            if (value == TimerDirection.BACKWARDS) {
                backwardsStartTime = time
            }
        }
    var joinWhileRunning: List<AccessLevel>
        get() = (Config.timerConfig.getSettingList("joinWhileRunning") ?: listOf(AccessLevel.OPERATOR.key)).map {
            fromKey(it as String) ?: AccessLevel.OPERATOR
        }.distinct()
        set(value) = Config.timerConfig.setSetting("joinWhileRunning", value.map { it.key })
    var color: TextColor
        get() = TextColor.color(Config.timerConfig.getSetting("color") as? Int ?: KColors.RED.color.rgb)
        set(value) = Config.timerConfig.setSetting("color", value.value())
    var time: Long
        get() = (Config.timerDataConfig.getSetting("time") ?: 0).toString().toLong()
        set(value) {
            Config.timerDataConfig.setSetting("time", value)
            if (direction == TimerDirection.BACKWARDS && (backwardsStartTime == 0L || backwardsStartTime < value)) {
                backwardsStartTime = time
            }
        }
    var backwardsStartTime: Long
        get() = Config.timerDataConfig.getSetting("startTime") as? Long ?: 0
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
                        if (time <= 1L) {
                            sync {
                                stop()
                                onlinePlayers.forEach {
                                    it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 0.5F, 1F)
                                    it.sendPrefixMessage(
                                        translatable("challenge.lose")
                                            .args(
                                                translatable("timer.backwards.time_up"),
                                                text(toString())
                                            )
                                    )
                                }
                                backwardsStartTime = 0
                            }
                        } else {
                            time--
                        }
                    }
                }
                ChallengeManager.challenges
                    .forEach { challenge ->
                        if (challenge.active) {
                            challenge.update()
                        }
                    }
                broadcastTimer()
            } else {
                broadcastIdle()
                onlinePlayers.filter { it.isOp && !it.inventory.contains(getSettingsItem(it.locale())) }.forEach {
                    it.inventory.setItem(8, getSettingsItem(it.locale()))
                }
            }
        }
    }

    private fun broadcastTimer() {
        onlinePlayers.forEach {
            it.sendActionBar(
                literalText {
                    component(formatTime())
                    if (additionalInfo.isNotEmpty()) {
                        component(space())
                        text("(")
                        text(additionalInfo.joinToString(" "))
                        text(")")
                    }
                    color = this@Timer.color
                    bold = true
                }
            )
        }
    }

    private fun broadcastIdle() {
        onlinePlayers.forEach {
            it.sendActionBar(
                translatable("timer.idle")
                    .color(color)
                    .decorate(TextDecoration.BOLD)
            )
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
            GameChangeManager.gameChanges
                .filter { it.active }
                .forEach { change ->
                    change.run()
                    change.onTimerToggle()
                }
            ChallengeManager.challenges
                .filter { it.active }
                .forEach { challenge ->
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
                player.getNearbyEntities(48.0, 48.0, 48.0)
                    .forEach {
                        if (it is Creature)
                            it.target = null
                    }
                if (player.hasPermission(Permissions.SETTINGS_ITEM)) {
                    player.inventory.setItem(8, getSettingsItem(player.locale()))
                }
            }
            ChallengeManager.unregisterChallengeListeners()
            GameChangeManager.unregisterGameChangeListeners()
            GoalManager.unregisterActiveGoal()
            running = false
            ChallengeManager.challenges
                .filter { it.active }
                .forEach { challenge ->
                    challenge.onTimerToggle()
                }
            GameChangeManager.gameChanges
                .filter { it.active }
                .forEach { change ->
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
        formatTime().plainText()
    } else {
        formatTime(backwardsStartTime).plainText()
    }

    @OptIn(ExperimentalTime::class)
    fun formatTime(
        time: Long = this.time,
    ): Component {
        val duration = time.seconds
        duration.toComponents(
            action = { days, hours, minutes, seconds, _ ->
                return literalText {
                    if (days != 0L) {
                        text(days.toString())
                        if (hours + minutes + seconds == 0) {
                            if (days > 1L) {
                                component(
                                    translatable("generic.long.days")
                                )
                            } else {
                                component(
                                    translatable("generic.long.day")
                                )
                            }
                        } else {
                            component(
                                translatable("generic.short.day")
                            )
                        }
                        component(space())
                    }
                    if (hours != 0) {
                        text(hours.toString())
                        if (days + minutes + seconds == 0L) {
                            if (hours > 1L) {
                                component(
                                    translatable("generic.long.hours")
                                )
                            } else {
                                component(
                                    translatable("generic.long.hour")
                                )
                            }
                        } else {
                            component(
                                translatable("generic.short.hour")
                            )
                        }
                        component(space())
                    }
                    if (minutes != 0) {
                        text(minutes.toString())
                        if (days + hours + seconds == 0L) {
                            if (minutes > 1L) {
                                component(
                                    translatable("generic.long.minutes")
                                )
                            } else {
                                component(
                                    translatable("generic.long.minute")
                                )
                            }
                        } else {
                            component(
                                translatable("generic.short.minute")
                            )
                        }
                        component(space())
                    }
                    if (seconds != 0) {
                        text(seconds.toString())
                        if (days + hours + minutes == 0L) {
                            if (seconds > 1L) {
                                component(
                                    translatable("generic.long.seconds")
                                )
                            } else {
                                component(
                                    translatable("generic.long.second")
                                )
                            }
                        } else {
                            component(
                                translatable("generic.short.second")
                            )
                        }
                    }
                    color = this@Timer.color
                }
            }
        )
    }
}
