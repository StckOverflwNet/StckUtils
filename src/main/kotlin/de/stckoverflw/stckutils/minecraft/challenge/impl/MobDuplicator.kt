package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.coloredString
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.util.GUIPage
import de.stckoverflw.stckutils.util.getGoBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Material
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.Locale

object MobDuplicator : Challenge() {

    private var exponentialAmount: Int
        get() = Config.challengeDataConfig.getSetting(id, "exponentialAmount") as Int? ?: 2
        set(value) = Config.challengeDataConfig.setSetting(id, "exponentialAmount", value)
    private var isExponential: Boolean
        get() = Config.challengeConfig.getSetting(id, "isExponential") as Boolean? ?: false
        set(value) = Config.challengeConfig.setSetting(id, "isExponential", value)

    override val id: String = "mob-duplicator"
    override val material: Material = Material.SUSPICIOUS_STEW
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = translatable(nameKey).coloredString(locale)
        defaultPage = 0

        page(0) {
            placeholder(Slots.Border, placeHolderItemGray)
            placeholder(Slots.BorderPaddingOne, placeHolderItemWhite)

            button(
                Slots.RowThreeSlotOne,
                getGoBackItem(locale)
            ) {
                it.player.openGUI(settingsGUI(locale), GUIPage.challengesPageNumber)
            }

            button(
                Slots.RowThreeSlotFive,
                exponentialItem(locale)
            ) {
                it.bukkitEvent.isCancelled = true
                if (it.bukkitEvent.isShiftClick) {
                    exponentialAmount = 2
                } else {
                    isExponential = !isExponential
                }
                it.bukkitEvent.currentItem = exponentialItem(locale)
            }
        }
    }

    private fun exponentialItem(locale: Locale) = itemStack(Material.RABBIT_STEW) {
        meta {
            name = translatable("$id.exponential_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.exponential_item.lore")
                        .args(
                            if (isExponential) {
                                text(" \nShift Click to reset amount\n")
                            } else {
                                empty()
                            },
                            if (isExponential) {
                                translatable("generic.enabled")
                            } else {
                                translatable("generic.disabled")
                            }
                        )
                        .render(locale)
                )
            }
        }
    }

    @EventHandler
    fun onDeath(event: EntityDamageByEntityEvent) {
        if (event.entity !is Mob && event.damager !is Player) {
            return
        }
        val mob = event.entity as Mob
        val player = event.damager as Player

        if (!player.isPlaying() ||
            mob.health - event.damage > 0
        ) {
            return
        }

        for (i in 1..exponentialAmount) {
            event.entity.world.spawnEntity(event.entity.location, event.entity.type)
        }
        if (isExponential && exponentialAmount * 2 <= 64) exponentialAmount *= 2
    }
}
