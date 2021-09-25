package de.stckoverflw.stckutils.config.impl

import de.stckoverflw.stckutils.config.AbstractConfig

class ResetSettingsConfig : AbstractConfig("resetsettings.yml") {

    var shouldReset: Boolean
        get() = yaml.getBoolean("shouldReset")
        set(value) {
            yaml.set("shouldReset", value)
            save()
        }

    var villageSpawn: Boolean
        get() = yaml.getBoolean("villageSpawn")
        set(value) {
            yaml.set("villageSpawn", value)
            save()
        }
}
