package de.stckoverflw.stckutils.config

object Config {

    lateinit var resetSettings: ResetSettings
    lateinit var challengeSettings: ChallengeSettings

    operator fun invoke() {
        reloadConfig()
    }

    fun reloadConfig() {
        resetSettings = ResetSettings()
        challengeSettings = ChallengeSettings()
    }
}
