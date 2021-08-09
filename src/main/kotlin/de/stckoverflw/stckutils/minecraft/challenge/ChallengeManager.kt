package de.stckoverflw.stckutils.minecraft.challenge

import de.stckoverflw.stckutils.minecraft.challenge.impl.*
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigotMainInstance

object ChallengeManager {

    val challenges = HashMap<Challenge, Boolean>()

    operator fun invoke() {
        challenges[InventoryDamageClear] = false
        challenges[SingleUse] = false
        challenges[GamerChallenge] = false
        challenges[ChunkFlattener] = false
        challenges[NoXP] = false
    }

    fun registerChallengeListeners() {
        challenges.forEach { (challenge, active) ->
            challenge.unregister()
            if (active) {
                if (challenge.usesEvents) {
                    pluginManager.registerEvents(challenge, KSpigotMainInstance)
                }
            }
        }
    }

    fun unregisterChallengeListeners() {
        challenges.forEach { (challenge, _) ->
            challenge.unregister()
        }
    }

    fun getChallenge(id: String): Challenge? {
        challenges.forEach { (challenge, _) ->
            if (challenge.id.equals(id, true)) {
                return challenge
            }
        }
        return null
    }
}
