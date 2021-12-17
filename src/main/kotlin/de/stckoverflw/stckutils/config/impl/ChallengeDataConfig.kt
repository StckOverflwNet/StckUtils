package de.stckoverflw.stckutils.config.impl

import de.stckoverflw.stckutils.config.AbstractDataConfig
import net.axay.kspigot.main.KSpigotMainInstance
import org.bukkit.Location
import java.util.*

class ChallengeDataConfig : AbstractDataConfig("challenges.yml", "Challenges") {

    fun setSetting(challengeId: String, setting: String, value: Any) {
        yaml.set("$challengeId.$setting", value)
        save()
    }

    fun getSetting(challengeId: String, setting: String): Any? {
        return yaml.get("$challengeId.$setting")
    }

    fun setLocation(challengeId: String, setting: String, location: Location) {
        val path = "$challengeId.$setting"
        yaml.set("$path.World", location.world.uid.toString())
        yaml.set("$path.X", location.x)
        yaml.set("$path.Y", location.y)
        yaml.set("$path.Z", location.z)
        yaml.set("$path.Yaw", location.yaw)
        yaml.set("$path.Pitch", location.pitch)
        save()
    }

    fun getLocation(challengeId: String, setting: String): Location? {
        val path = "$challengeId.$setting"
        if (yaml.getString("$path.World") == null ||
            UUID.fromString(yaml.getString("$path.World")) == null
        ) {
            return null
        }
        return Location(
            KSpigotMainInstance.server.getWorld(UUID.fromString(yaml.getString("$path.World"))),
            yaml.getDouble("$path.X"),
            yaml.getDouble("$path.Y"),
            yaml.getDouble("$path.Z"),
            yaml.getDouble("$path.Yaw").toFloat(),
            yaml.getDouble("$path.Pitch").toFloat()
        )
    }

    fun getSettingList(challengeId: String, setting: String): MutableList<*>? {
        return yaml.getList("$challengeId.$setting")
    }
}
