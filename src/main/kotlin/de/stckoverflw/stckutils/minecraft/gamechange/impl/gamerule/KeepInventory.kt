package de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule

import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.minecraft.gamechange.GameRule
import de.stckoverflw.stckutils.minecraft.gamechange.active
import de.stckoverflw.stckutils.minecraft.gamechange.descriptionKey
import de.stckoverflw.stckutils.minecraft.gamechange.nameKey
import de.stckoverflw.stckutils.util.Colors
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.flags
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.Locale

object KeepInventory : GameRule() {

    override val id: String = "keep-inventory"
    override val usesEvents: Boolean = false

    override fun item(locale: Locale): ItemStack = itemStack(Material.COMMAND_BLOCK) {
        meta {
            name = translatable(nameKey)
                .color(Colors.GOAL_COMPOUND)
                .render(locale)
            addLore {
                addComponent(
                    translatable(descriptionKey)
                        .args(
                            if (active) {
                                translatable("$id.keep_inventory")
                                    .color(Colors.ACTIVE)
                            } else {
                                translatable("$id.keep_inventory_off")
                                    .color(Colors.INACTIVE)
                            }
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
        active = !active
        run()
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item(event.player.locale()))
    }

    override fun run() {
        Bukkit.getWorlds().forEach {
            it.setGameRule(org.bukkit.GameRule.KEEP_INVENTORY, active)
        }
    }
}
