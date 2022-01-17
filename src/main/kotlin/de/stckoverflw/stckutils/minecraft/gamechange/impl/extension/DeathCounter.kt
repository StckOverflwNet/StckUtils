package de.stckoverflw.stckutils.minecraft.gamechange.impl.extension

import de.stckoverflw.stckutils.extension.Colors
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.minecraft.gamechange.GameExtension
import de.stckoverflw.stckutils.minecraft.gamechange.active
import de.stckoverflw.stckutils.minecraft.gamechange.descriptionKey
import de.stckoverflw.stckutils.minecraft.gamechange.nameKey
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemFlag
import java.util.Locale

object DeathCounter : GameExtension() {

    override val id: String = "death-counter"
    override val usesEvents: Boolean = true

    private val bossbar = BossBar.bossBar(
        translatable("deaths", listOf(text(0))),
        1.0F,
        BossBar.Color.BLUE,
        BossBar.Overlay.PROGRESS
    )
    private var deaths = 0

    override fun item(locale: Locale) = itemStack(Material.WITHER_SKELETON_SKULL) {
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
                                translatable("generic.activated")
                                    .color(Colors.ACTIVE)
                            } else {
                                translatable("generic.disabled")
                                    .color(Colors.INACTIVE)
                            }
                        )
                    )
                        .color(Colors.GOAL_COMPOUND_SECONDARY)
                        .render(locale)
                )
                /*
                + "Shift Left-click to higher the deaths"
                + "Shift Right-click to lower the deaths"
                 */
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
        event.bukkitEvent.isCancelled = true
        if (event.bukkitEvent.isShiftClick) {
            if (event.bukkitEvent.isLeftClick) {
                deaths++
            } else if (event.bukkitEvent.isRightClick) {
                if (deaths > 0) {
                    deaths--
                }
            }
        } else {
            active = !active
        }
        run()
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item(event.player.locale()))
    }

    override fun run() {
        if (active) {
            onlinePlayers.forEach {
                it.showBossBar(bossbar)
                bossbar.progress(1.0F)
            }
            bossbar.name(translatable("deaths", listOf(text(deaths))))
        } else {
            onlinePlayers.forEach {
                it.hideBossBar(bossbar)
            }
        }
    }

    @EventHandler
    @Suppress("unused_parameter")
    fun onDeath(event: PlayerDeathEvent) {
        deaths++
        run()
    }
}
