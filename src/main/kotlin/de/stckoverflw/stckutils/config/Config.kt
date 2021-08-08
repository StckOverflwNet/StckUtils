package de.stckoverflw.stckutils.config

object Config {

    lateinit var resetSettings: ResetSettings

    operator fun invoke() {
        reloadConfig()
    }

    fun reloadConfig() {
        resetSettings = ResetSettings()
    }
}
