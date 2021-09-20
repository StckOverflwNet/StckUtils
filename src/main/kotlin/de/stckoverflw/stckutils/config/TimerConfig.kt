package de.stckoverflw.stckutils.config

class TimerConfig : AbstractConfig("timer.yml") {

    fun setSetting(setting: String, value: Any) {
        yaml.set(setting, value)
        save()
    }

    fun getSetting(setting: String): Any? {
        return yaml.get(setting)
    }
}
