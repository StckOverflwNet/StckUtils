package de.stckoverflw.stckutils.user

import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material

/*
 * GUI Items
 */

val placeHolderItem = itemStack(Material.GRAY_STAINED_GLASS_PANE) {
    meta {
        name = "§a "
    }
}
val goBackItem = itemStack(Material.PAPER) {
    meta {
        name = "§cGo back"
        addLore {
            + ""
            + "§7§oClick to go to the previous Page"
        }
    }
}

/*
 * Inventory Items
 */

val settingsItem = itemStack(Material.NETHER_STAR) {
    meta {
        name = "§cSettings"
    }
}