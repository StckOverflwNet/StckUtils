package de.stckoverflw.stckutils.config.impl

import de.stckoverflw.stckutils.config.AbstractDataConfig

class GameChangeDataConfig : AbstractDataConfig("gamechanges.yml", "GameChanges") {

    fun setSetting(gameChangeId: String, setting: String, value: Any) {
        yaml.set("$gameChangeId.$setting", value)
        save()
    }

    fun getSetting(gameChangeId: String, setting: String): Any? {
        return yaml.get("$gameChangeId.$setting")
    }

    fun getSettingList(gameChangeId: String, setting: String): MutableList<*>? {
        return yaml.getList("$gameChangeId.$setting")
    }
}
