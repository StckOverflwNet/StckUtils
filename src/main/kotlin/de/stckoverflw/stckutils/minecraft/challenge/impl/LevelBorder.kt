package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.active
import de.stckoverflw.stckutils.util.goBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.main.KSpigotMainInstance
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerLevelChangeEvent

object LevelBorder : Challenge() {

    private var xpLevel: Int
        get() = (Config.challengeDataConfig.getSetting(id, "xpLevel") ?: 0) as Int
        set(value) = Config.challengeDataConfig.setSetting(id, "xpLevel", value)
    private var xpProgress: Float
        get() = (Config.challengeDataConfig.getSetting(id, "xpProgress") ?: 0F) as Float
        set(value) = Config.challengeDataConfig.setSetting(id, "xpProgress", value)
    private var isFirstRun
        get() = (Config.challengeConfig.getSetting(MobDuplicator.id, "isFirstRun") ?: true) as Boolean
        set(value) = Config.challengeConfig.setSetting(MobDuplicator.id, "isFirstRun", value)

    override val id: String = "level-border"
    override val name: String = "§eLevel Border"
    override val material: Material = Material.EXPERIENCE_BOTTLE
    override val description: List<String> = listOf(
        " ",
        "§7You start with a worldborder of 1.",
        "§7It will increase and decrease",
        "§7as your xp level changes."
    )
    override val usesEvents: Boolean = true

    override fun onToggle() {
        if (!active) {
            KSpigotMainInstance.server.worlds.forEach {
                it.worldBorder.reset()
            }
        } else {
            val worlds = KSpigotMainInstance.server.worlds
            val overWorld = worlds.first {
                it.environment == World.Environment.NORMAL
            }
            onlinePlayers.forEach { player ->
                player.teleportAsync(overWorld.spawnLocation)
                if (isFirstRun) {
                    player.level = 0
                    player.exp = 0F
                }
            }
            isFirstRun = false
            worlds.forEach {
                it.worldBorder.center = it.spawnLocation
                it.worldBorder.size = xpLevel.toDouble()
            }
        }
    }

    override fun configurationGUI(): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = name
        defaultPage = 0
        page(0) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, goBackItem) { it.player.openGUI(settingsGUI(), 1) }

            button(Slots.RowThreeSlotFive, resetItem()) {
                it.bukkitEvent.isCancelled = true
                isFirstRun = true
                xpLevel = 0
                xpProgress = 0F
                onToggle()
            }
        }
    }

    private fun resetItem() = itemStack(Material.BARRIER) {
        meta {
            name = "§4Reset"
            addLore {
                +" "
                +"§7Reset the Border and Level"
            }
        }
    }

    @EventHandler
    fun onXpProgress(event: PlayerExpChangeEvent) {
        xpProgress = event.player.exp - event.amount / onlinePlayers.size
        event.amount = 0
        onlinePlayers.forEach {
            it.exp = xpProgress
        }
    }

    @EventHandler
    fun onXpLevel(event: PlayerLevelChangeEvent) {
        xpLevel = event.player.level
        onlinePlayers.forEach {
            it.level = xpLevel
        }
        event.player.world.worldBorder.size = (xpLevel + 1).toDouble()
    }
}
