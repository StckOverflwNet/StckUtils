package de.stckoverflw.stckutils.minecraft.goal

import de.stckoverflw.stckutils.minecraft.goal.impl.battle.BakeCake
import de.stckoverflw.stckutils.minecraft.goal.impl.battle.Survive
import de.stckoverflw.stckutils.minecraft.goal.impl.teamgoal.AllAdvancements
import de.stckoverflw.stckutils.minecraft.goal.impl.teamgoal.AllItems
import de.stckoverflw.stckutils.minecraft.goal.impl.teamgoal.AllMobs
import de.stckoverflw.stckutils.minecraft.goal.impl.teamgoal.FindDiamond
import de.stckoverflw.stckutils.minecraft.goal.impl.teamgoal.GoToNether
import de.stckoverflw.stckutils.minecraft.goal.impl.teamgoal.KillEnderdragon
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
