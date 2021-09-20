package de.stckoverflw.stckutils.config

class GoalConfig : AbstractConfig("goals.yml") {

    fun getActive(goalId: String): Boolean {
        return if (yaml.contains("$goalId.active")) {
            yaml.getBoolean("$goalId.active")
        } else {
            yaml.set("$goalId.active", false)
            save()
            false
        }
    }

    fun setActive(goalId: String, active: Boolean) {
        yaml.set("$goalId.active", active)
        save()
    }

    fun setSetting(goalId: String, setting: String, value: Any) {
        yaml.set("$goalId.$setting", value)
        save()
    }

    fun getSetting(goalId: String, setting: String): Any? {
        return yaml.get("$goalId.$setting")
    }

    fun getSettingList(goalId: String, setting: String): MutableList<*>? {
        return yaml.getList("$goalId.$setting")
    }
}
