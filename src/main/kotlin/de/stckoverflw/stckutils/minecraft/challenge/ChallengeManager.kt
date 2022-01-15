package de.stckoverflw.stckutils.minecraft.challenge

import de.stckoverflw.stckutils.minecraft.challenge.impl.AdvancementDamage
import de.stckoverflw.stckutils.minecraft.challenge.impl.AntiArmor
import de.stckoverflw.stckutils.minecraft.challenge.impl.BalanceLife
import de.stckoverflw.stckutils.minecraft.challenge.impl.BlockExplode
import de.stckoverflw.stckutils.minecraft.challenge.impl.ChunkFlattener
import de.stckoverflw.stckutils.minecraft.challenge.impl.DamageFreeze
import de.stckoverflw.stckutils.minecraft.challenge.impl.DamageSwap
import de.stckoverflw.stckutils.minecraft.challenge.impl.GamerChallenge
import de.stckoverflw.stckutils.minecraft.challenge.impl.IceWalker
import de.stckoverflw.stckutils.minecraft.challenge.impl.InventoryDamageClear
import de.stckoverflw.stckutils.minecraft.challenge.impl.InventorySwap
import de.stckoverflw.stckutils.minecraft.challenge.impl.InvisibleEntities
import de.stckoverflw.stckutils.minecraft.challenge.impl.JackHammer
import de.stckoverflw.stckutils.minecraft.challenge.impl.LevelBorder
import de.stckoverflw.stckutils.minecraft.challenge.impl.Medusa
import de.stckoverflw.stckutils.minecraft.challenge.impl.MobDuplicator
import de.stckoverflw.stckutils.minecraft.challenge.impl.MobMagnet
import de.stckoverflw.stckutils.minecraft.challenge.impl.NoBlockBreak
import de.stckoverflw.stckutils.minecraft.challenge.impl.NoBlockPlace
import de.stckoverflw.stckutils.minecraft.challenge.impl.NoCrafting
import de.stckoverflw.stckutils.minecraft.challenge.impl.NoDeath
import de.stckoverflw.stckutils.minecraft.challenge.impl.NoFallDamage
import de.stckoverflw.stckutils.minecraft.challenge.impl.NoSneak
import de.stckoverflw.stckutils.minecraft.challenge.impl.NoVillagerTrade
import de.stckoverflw.stckutils.minecraft.challenge.impl.NoXP
import de.stckoverflw.stckutils.minecraft.challenge.impl.RandomEffect
import de.stckoverflw.stckutils.minecraft.challenge.impl.RandomItem
import de.stckoverflw.stckutils.minecraft.challenge.impl.Randomizer
import de.stckoverflw.stckutils.minecraft.challenge.impl.SingleUse
import de.stckoverflw.stckutils.minecraft.challenge.impl.Snake
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigotMainInstance

object ChallengeManager {

    lateinit var challenges: ArrayList<Challenge>

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
