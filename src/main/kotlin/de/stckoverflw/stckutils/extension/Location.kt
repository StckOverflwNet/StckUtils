package de.stckoverflw.stckutils.extension

import net.minecraft.core.BlockPos
import org.bukkit.Location

fun BlockPos(location: Location): BlockPos {
    return BlockPos(location.blockX, location.blockY, location.blockZ)
}
