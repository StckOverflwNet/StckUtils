package de.stckoverflw.stckutils.minecraft.goal

import de.stckoverflw.stckutils.minecraft.goal.impl.AllAdvancements
import de.stckoverflw.stckutils.minecraft.goal.impl.AllItems
import de.stckoverflw.stckutils.minecraft.goal.impl.AllMobs
import de.stckoverflw.stckutils.minecraft.goal.impl.BakeCake
import de.stckoverflw.stckutils.minecraft.goal.impl.FindDiamond
import de.stckoverflw.stckutils.minecraft.goal.impl.GoToNether
import de.stckoverflw.stckutils.minecraft.goal.impl.KillEnderdragon
import de.stckoverflw.stckutils.minecraft.goal.impl.Survive
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
