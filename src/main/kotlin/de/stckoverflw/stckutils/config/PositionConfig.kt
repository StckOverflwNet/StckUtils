package de.stckoverflw.stckutils.config

import de.stckoverflw.stckutils.config.data.PositionData
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

class PositionConfig : AbstractConfig("positions.yml") {

    val positions = ArrayList<PositionData>()

    init {
        yaml.getKeys(false).forEach {
            Bukkit.getLogger().info("§aFound Position: $it")
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
