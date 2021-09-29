package de.stckoverflw.stckutils.minecraft.goal

import de.stckoverflw.stckutils.minecraft.goal.impl.*
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigotMainInstance

object GoalManager {

    lateinit var goals: ArrayList<Goal>
    var activeGoal: Goal? = null

    operator fun invoke() {
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
        // TODO: ALL death messages +  adjustments to AllAdvancements gui
    }

    fun registerActiveGoal() {
        goals.forEach {
            it.unregister()
            if (it.active) {
                activeGoal = it
            }
        }
        if (activeGoal != null) {
            pluginManager.registerEvents(activeGoal!!, KSpigotMainInstance)
        }
    }

    fun unregisterActiveGoal() {
        activeGoal?.unregister()
    }
}
