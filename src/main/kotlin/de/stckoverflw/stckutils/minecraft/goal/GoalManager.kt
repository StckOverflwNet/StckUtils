package de.stckoverflw.stckutils.minecraft.goal

import de.stckoverflw.stckutils.minecraft.goal.impl.BakeCake
import de.stckoverflw.stckutils.minecraft.goal.impl.FindDiamond
import de.stckoverflw.stckutils.minecraft.goal.impl.GoToNether
import de.stckoverflw.stckutils.minecraft.goal.impl.KillEnderdragon
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
            BakeCake
        )
    }

    fun registerActiveGoal() {
        goals.forEach {
            it.unregister()
        }
        if (activeGoal != null) {
            pluginManager.registerEvents(activeGoal!!, KSpigotMainInstance)
        }
    }
}