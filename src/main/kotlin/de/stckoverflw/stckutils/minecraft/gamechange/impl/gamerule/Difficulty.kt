package de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.minecraft.gamechange.GameRule
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Difficulty : GameRule() {
    override val id: String = "difficulty"

    override fun item(): ItemStack = itemStack(Material.END_CRYSTAL) {
        meta {
            name = "§bDifficulty"
            addLore {
                + " "
                + "§7Sets the Difficulty of the Server"
                + " "
                + "§7Current Difficulty: §b${difficulty.name}"
            }
        }
    }

    override val usesEvents: Boolean = false

    private var difficulty: Difficulty
        get() = Difficulty.valueOf(Config.gameChangeConfig.getSetting(id, "difficulty") as String? ?: "NORMAL")
        set(value) = Config.gameChangeConfig.setSetting(id, "difficulty", value.name)


    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        difficulty = when (difficulty) {
            Difficulty.PEACEFUL -> {
                Difficulty.EASY
            }
            Difficulty.EASY -> {
                Difficulty.NORMAL
            }
            Difficulty.NORMAL -> {
                Difficulty.HARD
            }
            Difficulty.HARD -> {
                Difficulty.PEACEFUL
            }
        }
        run()
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item())
    }

    override fun run() {
        Bukkit.getWorlds().forEach {
            it.difficulty = difficulty
        }
    }
}