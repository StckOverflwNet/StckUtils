package de.stckoverflw.stckutils.minecraft.challenge

import de.stckoverflw.stckutils.i18n.TranslationsProvider
import de.stckoverflw.stckutils.minecraft.challenge.impl.*
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigotMainInstance

object ChallengeManager {

    lateinit var challenges: ArrayList<Challenge>
    lateinit var translationsProvider: TranslationsProvider

    operator fun invoke() {
        challenges = arrayListOf(
            AntiArmor,
            InventoryDamageClear,
            SingleUse,
            GamerChallenge,
            BlockExplode,
            ChunkFlattener,
            NoXP,
            NoBlockBreak,
            NoBlockPlace,
            NoVillagerTrade,
            NoCrafting,
            InvisibleEntities,
            NoFallDamage,
            RandomEffect,
            NoSneak,
            NoDeath,
            BalanceLife,
            AdvancementDamage,
            Snake,
            JackHammer,
            RandomItem,
            MobDuplicator,
            MobMagnet,
            InventorySwap,
            Randomizer,
            LevelBorder,
            IceWalker,
            Medusa,
            DamageSwap,
            DamageFreeze,
        )
        challenges.sortBy { it.id }
        translationsProvider = TranslationsProvider("translations.minecraft.challenge")
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
