package de.stckoverflw.stckutils.minecraft.challenge
import de.stckoverflw.stckutils.minecraft.challenge.impl.BlockExplode
import de.stckoverflw.stckutils.minecraft.challenge.impl.GamerChallenge
import de.stckoverflw.stckutils.minecraft.challenge.impl.InventoryDamageClear
import de.stckoverflw.stckutils.minecraft.challenge.impl.SingleUse
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigotMainInstance

object ChallengeManager {

    lateinit var challenges: ArrayList<Challenge>

    operator fun invoke() {
        challenges = arrayListOf(
            InventoryDamageClear,
            SingleUse,
            GamerChallenge,
            BlockExplode
        )
    }

    fun registerChallengeListeners() {
        challenges.forEach { challenge ->
            challenge.unregister()
            if (challenge.active) {
                if (challenge.usesEvents) {
                    pluginManager.registerEvents(challenge, KSpigotMainInstance)
                }
            }
        }
    }

    fun unregisterChallengeListeners() {
        challenges.forEach { challenge ->
            challenge.unregister()
        }
    }

    fun getChallenge(id: String): Challenge? {
        challenges.forEach { challenge ->
            if (challenge.id.equals(id, true)) {
                return challenge
            }
        }
        return null
    }
}