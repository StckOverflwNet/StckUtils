package de.stckoverflw.stckutils.minecraft.gamechange.impl.extension

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.minecraft.challenge.descriptionKey
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.gamechange.GameExtension
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import java.util.*

object MaxHealth : GameExtension() {

    private var health: Int
        get() = Config.gameChangeConfig.getSetting(id, "max-health") as Int? ?: 20
        set(value) = Config.gameChangeConfig.setSetting(id, "max-health", value)

    override val id: String = "max-health"
    override val usesEvents: Boolean = false

    override fun item(locale: Locale) = itemStack(Material.REDSTONE) {
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
                    id
                ).split("\n").forEach {
                    +it
                }
            }
        }
    }

    override fun click(event: GUIClickEvent<ForInventoryFiveByNine>) {
        if (event.bukkitEvent.isLeftClick) {
            if (health >= 10) {
                if (health <= 90) {
                    health += 10
                }
            } else {
                health += 1
            }
        } else if (event.bukkitEvent.isRightClick) {
            if (health >= 10) {
                health -= 10
            } else {
                if (health >= 1) {
                    health -= 1
                }
            }
        }
        run()
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item(event.player.language))
    }

    override fun run() {
        Bukkit.getOnlinePlayers().forEach {
            it.isHealthScaled = true
            it.healthScale = health.toDouble()
            it.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = health.toDouble()
            it.health = health.toDouble()
        }
    }
}
