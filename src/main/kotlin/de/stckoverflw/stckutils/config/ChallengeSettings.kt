package de.stckoverflw.stckutils.config

class ChallengeSettings : AbstractConfig("challenges.yml") {

    fun getActive(challengeId: String): Boolean {
        return if (yaml.contains("$challengeId.active")) {
            yaml.getBoolean("$challengeId.active")
        } else {
            yaml.set("$challengeId.active", false)
            save()
            false
        }
    }

    fun setActive(challengeId: String, active: Boolean) {
        yaml.set("$challengeId.active", active)
        save()
    }

    fun setSetting(challengeId: String, setting: String, value: Any) {
        yaml.set("$challengeId.$setting", value)
        save()
    }

    fun getSetting(challengeId: String, setting: String): Any? {
        return yaml.get("$challengeId.$setting")
    }
}
