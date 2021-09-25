package de.stckoverflw.stckutils.config.impl

import de.stckoverflw.stckutils.config.AbstractDataConfig

class AllItemsDataConfig : AbstractDataConfig("allitems.yml", "AllX") {

    fun setSetting(setting: String, value: Any?) {
        yaml.set(setting, value)
        save()
    }

    fun getSetting(setting: String): Any? {
        return yaml.get(setting)
    }

    fun getSettingList(setting: String): MutableList<*>? {
        return yaml.getList(setting)
    }
}
