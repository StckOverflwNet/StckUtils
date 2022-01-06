package de.stckoverflw.stckutils.minecraft.challenge.impl

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import java.util.*

object NoXP : Challenge() {

    override val id: String = "no-xp"
    override val material: Material = Material.EXPERIENCE_BOTTLE
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null

    @EventHandler
    fun onXP(event: PlayerPickupExperienceEvent) {
        if (!event.player.isPlaying()) {
            return
        }
        lose(id, arrayOf(event.player.name))
    }
}
