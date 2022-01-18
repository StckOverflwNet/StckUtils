package de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.asTextColor
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.minecraft.gamechange.GameRule
import de.stckoverflw.stckutils.minecraft.gamechange.descriptionKey
import de.stckoverflw.stckutils.minecraft.gamechange.nameKey
import de.stckoverflw.stckutils.util.Colors
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.flags
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.Locale

object Difficulty : GameRule() {

    private var difficulty: Difficulty
        get() = Difficulty.valueOf(Config.gameChangeConfig.getSetting(id, "difficulty") as String? ?: "NORMAL")
        set(value) = Config.gameChangeConfig.setSetting(id, "difficulty", value.name)

    override val id: String = "difficulty"
    override val usesEvents: Boolean = false

    override fun item(locale: Locale): ItemStack = itemStack(Material.END_CRYSTAL) {
        meta {
            name = translatable(nameKey)
                .color(Colors.GOAL_COMPOUND)
                .render(locale)
            addLore {
                addComponent(
                    translatable(descriptionKey)
                        .args(
                            translatable(difficulty.translationKey())
                                .color(KColors.DARKGRAY.asTextColor())
                        )
                        .color(Colors.GOAL_COMPOUND_SECONDARY)
                        .render(locale)
                )
            }

            flags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            )
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
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item(event.player.locale()))
    }

    override fun run() {
        Bukkit.getWorlds().forEach {
            it.difficulty = difficulty
        }
    }
}
