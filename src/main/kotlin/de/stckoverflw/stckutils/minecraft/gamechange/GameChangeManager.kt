package de.stckoverflw.stckutils.minecraft.gamechange
import de.stckoverflw.stckutils.i18n.TranslationsProvider
import de.stckoverflw.stckutils.minecraft.gamechange.impl.extension.DamageMultiplier
import de.stckoverflw.stckutils.minecraft.gamechange.impl.extension.DeathCounter
import de.stckoverflw.stckutils.minecraft.gamechange.impl.extension.MaxHealth
import de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule.AllowPvP
import de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule.Difficulty
import de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule.KeepInventory
import de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule.SpawnWorld
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigotMainInstance

object GameChangeManager {

    lateinit var gameChanges: ArrayList<GameChange>
    lateinit var translationsProvider: TranslationsProvider

    operator fun invoke() {
        gameChanges = arrayListOf(
            MaxHealth,
            DeathCounter,
            AllowPvP,
            Difficulty,
            KeepInventory,
            DamageMultiplier,
            SpawnWorld,
        )
        gameChanges.sortBy { it.id }
        translationsProvider = TranslationsProvider("translations.minecraft.gamechange")
    }

    fun registerGameChangeListeners() {
        gameChanges.forEach { change ->
            change.unregister()
            if (change.usesEvents) {
                pluginManager.registerEvents(change, KSpigotMainInstance)
            }
        }
    }

    fun unregisterGameChangeListeners() {
        gameChanges.forEach { change ->
            change.unregister()
        }
    }

    fun getGameChange(id: String): GameChange? {
        gameChanges.forEach { change ->
            if (change.id.equals(id, true)) {
                return change
            }
        }
        return null
    }
}
