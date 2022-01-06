package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.util.getGoBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*

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
        title = ChallengeManager.translationsProvider.translate(
            nameKey,
            locale,
            id
        )
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, getGoBackItem(locale)) { it.player.openGUI(settingsGUI(locale), 1) }

            button(Slots.RowThreeSlotFive, exponentialItem(locale)) {
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
            name = ChallengeManager.translationsProvider.translate(
                "exponential_item.name",
                locale,
                id
            )
            addLore {
                ChallengeManager.translationsProvider.translate(
                    "exponential_item.lore",
                    locale,
                    id,
                    arrayOf(
                        if (isExponential) {
                            " \n§7 Shift Click to reset amount\n"
                        } else {
                            ""
                        },
                        if (isExponential) {
                            StckUtilsPlugin.translationsProvider.translate(
                                "generic.enabled",
                                locale,
                                "general"
                            )
                        } else {
                            StckUtilsPlugin.translationsProvider.translate(
                                "generic.disabled",
                                locale,
                                "general"
                            )
                        }
                    )
                ).split("\n").forEach {
                    +it
                }
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

        // ignore players that are currently not playing
        if (!player.isPlaying()) {
            return
        }

        // ignore the EnderDragon and mobs that do not die after the damage is applied
        if (mob is EnderDragon || mob.health - event.damage > 0) {
            return
        }

        for (i in 1..exponentialAmount) {
            event.entity.world.spawnEntity(event.entity.location, event.entity.type)
        }
        if (isExponential && exponentialAmount * 2 <= 64) exponentialAmount *= 2
    }
}
