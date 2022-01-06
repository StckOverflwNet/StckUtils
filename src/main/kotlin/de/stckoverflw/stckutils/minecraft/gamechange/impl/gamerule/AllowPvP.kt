package de.stckoverflw.stckutils.minecraft.gamechange.impl.gamerule

import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.minecraft.challenge.descriptionKey
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.gamechange.GameRule
import de.stckoverflw.stckutils.minecraft.gamechange.active
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Material
import java.util.*

object AllowPvP : GameRule() {

    override val id: String = "allow-pvp"
    override val usesEvents: Boolean = false

    override fun item(locale: Locale) = itemStack(Material.DIAMOND_SWORD) {
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
                    arrayOf(
                        if (active) {
                            GameChangeManager.translationsProvider.translate(
                                "players_can_not_attack_each_other",
                                locale,
                                id
                            )
                        } else {
                            GameChangeManager.translationsProvider.translate(
                                "players_can_attack_each_other",
                                locale,
                                id
                            )
                        }
                    )
                )
            }
        }
    }

    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        active = !active
        run()
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item(event.player.language))
    }

    override fun run() {
        Bukkit.getWorlds().forEach {
            it.pvp = !active
        }
    }
}
