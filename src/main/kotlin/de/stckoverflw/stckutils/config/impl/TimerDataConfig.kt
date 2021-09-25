package de.stckoverflw.stckutils.config.impl

import de.stckoverflw.stckutils.config.AbstractDataConfig

class TimerDataConfig : AbstractDataConfig("timer.yml", "Timer") {

    fun setSetting(setting: String, value: Any) {
        yaml.set(setting, value)
        save()
    }

    fun getSetting(setting: String): Any? {
        return yaml.get(setting)
    }
}
