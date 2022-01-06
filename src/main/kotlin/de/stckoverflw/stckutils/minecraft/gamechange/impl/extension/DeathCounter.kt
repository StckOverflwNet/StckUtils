package de.stckoverflw.stckutils.minecraft.gamechange.impl.extension

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.minecraft.challenge.descriptionKey
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.gamechange.GameExtension
import de.stckoverflw.stckutils.minecraft.gamechange.active
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUIClickEvent
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*

object DeathCounter : GameExtension() {

    override val id: String = "death-counter"
    override val usesEvents: Boolean = true

    private val bossbar = Bukkit.createBossBar(
        GameChangeManager.translationsProvider.translate(
            "deaths",
            Config.languageConfig.defaultLanguage,
            id,
            arrayOf(0)
        ),
        BarColor.BLUE, BarStyle.SOLID
    )
    private var deaths = 0

    override fun item(locale: Locale) = itemStack(Material.WITHER_SKELETON_SKULL) {
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
                            "§a" + StckUtilsPlugin.translationsProvider.translate(
                                "generic.activated",
                                locale,
                                "general"
                            )
                        } else {
                            "§c" + StckUtilsPlugin.translationsProvider.translate(
                                "generic.disabled",
                                locale,
                                "general"
                            )
                        }
                    )
                ).split("\n").forEach {
                    +it
                }
                /*
                + "§7Shift Left-click to higher the deaths"
                + "§7Shift Right-click to lower the deaths"
                 */
            }
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
        event.bukkitEvent.clickedInventory!!.setItem(event.bukkitEvent.slot, item(event.player.language))
    }

    override fun run() {
        if (active) {
            bossbar.isVisible = true
            Bukkit.getOnlinePlayers().forEach {
                bossbar.addPlayer(it)
                bossbar.progress = 1.0
            }
            bossbar.setTitle(
                GameChangeManager.translationsProvider.translate(
                    "deaths",
                    Config.languageConfig.defaultLanguage,
                    id,
                    arrayOf(deaths)
                )
            )
        } else {
            bossbar.isVisible = false
        }
    }

    @EventHandler
    @Suppress("unused_parameter")
    fun onDeath(event: PlayerDeathEvent) {
        deaths++
        run()
    }
}
