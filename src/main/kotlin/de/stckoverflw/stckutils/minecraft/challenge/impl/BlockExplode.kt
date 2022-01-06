package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
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
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import java.util.*

object BlockExplode : Challenge() {

    private var isFire: Boolean
        get() = Config.challengeConfig.getSetting(id, "isFire") as Boolean? ?: false
        set(value) = Config.challengeConfig.setSetting(id, "isFire", value)
    private var chance: Int
        get() = Config.challengeConfig.getSetting(id, "chance") as Int? ?: 50
        set(value) = Config.challengeConfig.setSetting(id, "chance", value)

    override val id: String = "block-explode"
    override val material: Material = Material.TNT
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        page(1) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, getGoBackItem(locale)) { it.player.openGUI(settingsGUI(locale), 1) }

            button(Slots.RowThreeSlotFour, generateChanceItem(locale)) {
                if (it.bukkitEvent.isLeftClick) {
                    if (chance <= 95) {
                        chance += 5
                    }
                } else if (it.bukkitEvent.isRightClick) {
                    if (chance >= 5) {
                        chance -= 5
                    }
                }
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateChanceItem(locale))
            }

            button(Slots.RowThreeSlotSix, generateFireItem(locale)) {
                isFire = !isFire
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateFireItem(locale))
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!event.player.isPlaying()) {
            return
        }
        if ((1..100).random() <= chance) {
            event.block.location.world.createExplosion(event.block.location, 3.3F, isFire)
        }
    }

    private fun generateChanceItem(locale: Locale) = itemStack(Material.SPRUCE_SIGN) {
        meta {
            name = ChallengeManager.translationsProvider.translate(
                "chance_item.name",
                locale,
                id
            )
            addLore {
                ChallengeManager.translationsProvider.translate(
                    "chance_item.lore",
                    locale,
                    id,
                    arrayOf(chance)
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }

    private fun generateFireItem(locale: Locale) = itemStack(Material.FIRE_CHARGE) {
        meta {
            name = ChallengeManager.translationsProvider.translate(
                "fire_item.name",
                locale,
                id
            )
            addLore {
                ChallengeManager.translationsProvider.translate(
                    "fire_item.lore",
                    locale,
                    id,
                    arrayOf(if (isFire) "§a$isFire" else "§c$isFire")
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }
}
