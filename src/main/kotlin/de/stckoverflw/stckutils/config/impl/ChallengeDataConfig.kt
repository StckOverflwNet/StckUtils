package de.stckoverflw.stckutils.config.impl

import de.stckoverflw.stckutils.config.AbstractDataConfig

class ChallengeDataConfig : AbstractDataConfig("challenges.yml", "Challenges") {

    fun setSetting(gameChangeId: String, setting: String, value: Any) {
        yaml.set("$gameChangeId.$setting", value)
        save()
    }

    fun getSetting(gameChangeId: String, setting: String): Any? {
        return yaml.get("$gameChangeId.$setting")
    }
}
