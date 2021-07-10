package de.stckoverflw.stckutils.goal

import de.stckoverflw.stckutils.goal.impl.FindDiamond
import de.stckoverflw.stckutils.goal.impl.GoToNether
import de.stckoverflw.stckutils.goal.impl.KillEnderdragon
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.main.KSpigotMainInstance

object GoalManager {

    lateinit var goals: ArrayList<Goal>
    var activeGoal: Goal? = null

    operator fun invoke() {
        goals = arrayListOf(
            KillEnderdragon,
            FindDiamond,
            GoToNether
        )
    }

    fun getGoal(id: String): Goal? {
        goals.forEach {
            if (it.id.equals(id, true)) {
                return it
            }
        }
        return null
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