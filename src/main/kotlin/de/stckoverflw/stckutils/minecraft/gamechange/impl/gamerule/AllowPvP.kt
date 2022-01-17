package de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule

import de.stckoverflw.stckutils.extension.Colors
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.minecraft.gamechange.GameRule
import de.stckoverflw.stckutils.minecraft.gamechange.active
import de.stckoverflw.stckutils.minecraft.gamechange.descriptionKey
import de.stckoverflw.stckutils.minecraft.gamechange.nameKey
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import java.util.Locale

object AllowPvP : GameRule() {

    override val id: String = "allow-pvp"
    override val usesEvents: Boolean = false

    override fun item(locale: Locale) = itemStack(Material.DIAMOND_SWORD) {
        meta {
            name = translatable(nameKey)
                .color(Colors.GOAL_COMPOUND)
                .render(locale)
            addLore {
                addComponent(
                    translatable(
                        descriptionKey,
                        listOf(
                            if (active) {
                                translatable("$id.players_can_not_attack_each_other")
                                    .color(Colors.INACTIVE)
                            } else {
                                translatable("$id.players_can_attack_each_other")
                                    .color(Colors.ACTIVE)
                            }
                        )
                    )
                        .color(Colors.GOAL_COMPOUND_SECONDARY)
                        .render(locale)
                )
            }
            addItemFlags(
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
        active = !active
        run()
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item(event.player.locale()))
    }

    override fun run() {
        Bukkit.getWorlds().forEach {
            it.pvp = !active
        }
    }
}
