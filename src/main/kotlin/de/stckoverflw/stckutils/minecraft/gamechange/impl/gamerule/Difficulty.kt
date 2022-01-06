package de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.minecraft.challenge.descriptionKey
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
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
import java.util.*

object Difficulty : GameRule() {

    private var difficulty: Difficulty
        get() = Difficulty.valueOf(Config.gameChangeConfig.getSetting(id, "difficulty") as String? ?: "NORMAL")
        set(value) = Config.gameChangeConfig.setSetting(id, "difficulty", value.name)

    override val id: String = "difficulty"
    override val usesEvents: Boolean = false

    override fun item(locale: Locale): ItemStack = itemStack(Material.END_CRYSTAL) {
        meta {
            name = GameChangeManager.translationsProvider.translate(
                nameKey,
                locale,
                id
            )
            addLore {
                GameChangeManager.translationsProvider.translate(
                    descriptionKey,
                    locale,
                    id,
                    arrayOf(difficulty.name)
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }

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
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item(event.player.language))
    }

    override fun run() {
        Bukkit.getWorlds().forEach {
            it.difficulty = difficulty
        }
    }
}
