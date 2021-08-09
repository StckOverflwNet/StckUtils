package de.stckoverflw.stckutils.minecraft.timer

import de.stckoverflw.stckutils.extension.fromBase64
import de.stckoverflw.stckutils.extension.toBase64
import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.user.settingsItem
import net.axay.kspigot.main.KSpigotMainInstance
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Creature
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

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
                ChallengeManager.challenges.forEach { (challenge, active) ->
                    if (active) {
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
            GameChangeManager.gameChanges.forEach { (change, active) ->
                if (active) {
                    change.run()
                }
            }
            ChallengeManager.registerChallengeListeners()
            ChallengeManager.challenges.forEach { (challenge, active) ->
                if (active) {
                    challenge.prepareChallenge()
                }
            }
            GoalManager.registerActiveGoal()
            Bukkit.getOnlinePlayers().forEach {
                it.inventory.clear()
                if (it.persistentDataContainer.has(NamespacedKey(KSpigotMainInstance, "challenge-inventory-contents"), PersistentDataType.STRING)) {
                    it.inventory.contents =
                        it.persistentDataContainer.get(NamespacedKey(KSpigotMainInstance, "challenge-inventory-contents"), PersistentDataType.STRING)
                            ?.let { it1 -> fromBase64(it1) } as Array<out ItemStack?>
                    it.persistentDataContainer.remove(NamespacedKey(KSpigotMainInstance, "challenge-inventory-contents"))
                }
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
            running = !running
            Bukkit.getOnlinePlayers().forEach { player ->
                player.persistentDataContainer.set(
                    NamespacedKey(KSpigotMainInstance, "challenge-inventory-contents"),
                    PersistentDataType.STRING,
                    toBase64(player.inventory.contents as Array<ItemStack>)
                )
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

    override fun toString(): String {
        val hours = time / 3600
        val minutes = (time % 3600) / 60
        val seconds = time % 60

        val timerString = StringBuilder("§c§l")

        if (hours > 0) timerString.append("${hours}h ")
        if (minutes > 0) timerString.append("${minutes}m ")
        if (seconds > 0) {
            if (hours > 0 || minutes > 0) {
                timerString.append("${seconds}s")
            } else {
                timerString.append("$seconds seconds")
            }
        }

        return timerString.toString()
    }
}
