package de.stckoverflw.stckutils.config.data

import org.bukkit.Location
import java.util.UUID

data class PositionData(
    val name: String,
    val creator: UUID,
    val location: Location,
)
