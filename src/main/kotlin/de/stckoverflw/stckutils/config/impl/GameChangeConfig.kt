package de.stckoverflw.stckutils.config.impl

import de.stckoverflw.stckutils.config.AbstractConfig

class GameChangeConfig : AbstractConfig("gamechanges.yml") {

    fun getActive(gameChangeId: String): Boolean {
        return if (yaml.contains("$gameChangeId.active")) {
            yaml.getBoolean("$gameChangeId.active")
        } else {
            yaml.set("$gameChangeId.active", false)
            save()
            false
        }
    }

    fun setActive(gameChangeId: String, active: Boolean) {
        yaml.set("$gameChangeId.active", active)
        save()
    }

    fun setSetting(gameChangeId: String, setting: String, value: Any) {
        yaml.set("$gameChangeId.$setting", value)
        save()
    }

    fun getSetting(gameChangeId: String, setting: String): Any? {
        return yaml.get("$gameChangeId.$setting")
    }
}
