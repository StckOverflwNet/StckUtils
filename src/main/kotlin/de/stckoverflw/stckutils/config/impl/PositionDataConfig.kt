package de.stckoverflw.stckutils.config.impl

import de.stckoverflw.stckutils.config.AbstractDataConfig
import de.stckoverflw.stckutils.config.data.PositionData
import org.bukkit.Location
import java.util.UUID

class PositionDataConfig : AbstractDataConfig("positions.yml", "Positions") {

    val positions = ArrayList<PositionData>()

    init {
        yaml.getKeys(false).forEach {
            positions.add(
                PositionData(
                    it,
                    UUID.fromString(yaml.getString("$it.creator")),
                    yaml.get("$it.location") as Location
                )
            )
        }
    }

    fun addPosition(positionData: PositionData) {
        yaml.set("${positionData.name}.creator", positionData.creator.toString())
        yaml.set("${positionData.name}.location", positionData.location)
        save()
        positions.add(positionData)
    }
}
