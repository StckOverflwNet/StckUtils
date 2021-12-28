package de.stckoverflw.stckutils.config.impl

import de.stckoverflw.stckutils.config.AbstractConfig

class HideConfig : AbstractConfig("hide.yml") {

    fun setSetting(setting: String, value: Any) {
        yaml.set(setting, value)
        save()
    }

    fun getSetting(setting: String): Any? {
        return yaml.get(setting)
    }
}
