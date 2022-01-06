package de.stckoverflw.stckutils.minecraft.goal

import de.stckoverflw.stckutils.i18n.TranslationsProvider
import de.stckoverflw.stckutils.minecraft.goal.impl.*
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigotMainInstance

object GoalManager {

    lateinit var goals: ArrayList<Goal>
    var activeGoal: Goal? = null
    lateinit var translationsProvider: TranslationsProvider

    operator fun invoke() {
        translationsProvider = TranslationsProvider("translations.minecraft.goal")
        goals = arrayListOf(
            KillEnderdragon,
            GoToNether,
            FindDiamond,
            BakeCake,
            Survive,
            AllMobs,
            AllItems,
            AllAdvancements,
        )
        goals.sortBy { it.id }

        setActiveGoal()
    }

    private fun setActiveGoal() {
        goals.forEach {
            it.unregister()
            if (it.active) {
                activeGoal = it
            }
        }
    }

    fun registerActiveGoal() {
        setActiveGoal()
        if (activeGoal != null) {
            pluginManager.registerEvents(activeGoal!!, KSpigotMainInstance)
        }
    }

    fun unregisterActiveGoal() {
        activeGoal?.unregister()
    }
}
