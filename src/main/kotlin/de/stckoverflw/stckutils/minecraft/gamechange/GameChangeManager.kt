package de.stckoverflw.stckutils.minecraft.gamechange

import de.stckoverflw.stckutils.minecraft.gamechange.impl.DeathCounter
import de.stckoverflw.stckutils.minecraft.gamechange.impl.MaxHealth
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigotMainInstance

object GameChangeManager {

    val gameChanges = HashMap<GameChange, Boolean>()

    operator fun invoke() {
        gameChanges[MaxHealth] = MaxHealth.defaultActivated
        gameChanges[DeathCounter] = DeathCounter.defaultActivated
    }

    fun registerGameChangeListeners() {
        gameChanges.forEach { (change, active) ->
            change.unregister()
            if (active) {
                if (change.usesEvents) {
                    pluginManager.registerEvents(change, KSpigotMainInstance)
                }
            }
        }
    }

    fun getGameChange(id: String): GameChange? {
        gameChanges.forEach { (change, _) ->
            if (change.id.equals(id, true)) {
                return change
            }
        }
        return null
    }

}