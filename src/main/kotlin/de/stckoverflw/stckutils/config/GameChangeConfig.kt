package de.stckoverflw.stckutils.config

class GameChangeConfig : AbstractConfig("gamechanges.yml") {

    fun setSetting(gameChangeId: String, setting: String, value: Any) {
        yaml.set("$gameChangeId.$setting", value)
        save()
    }

    fun getSetting(gameChangeId: String, setting: String): Any? {
        return yaml.get("$gameChangeId.$setting")
    }
}
